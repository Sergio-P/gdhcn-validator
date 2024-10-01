package co.entomo.gdhcn.service.impl;

import COSE.*;
import co.entomo.gdhcn.entity.IpsFile;
import co.entomo.gdhcn.entity.QrCode;
import co.entomo.gdhcn.exceptions.GdhcnIPSAlreadyAccessedException;
import co.entomo.gdhcn.exceptions.GdhcnQRCodeExpiredException;
import co.entomo.gdhcn.entity.RecipientKey;
import co.entomo.gdhcn.exceptions.GdhcnValidationException;
import co.entomo.gdhcn.hcert.GreenCertificateDecoder;
import co.entomo.gdhcn.hcert.GreenCertificateEncoder;
import co.entomo.gdhcn.repository.IpsFileRepository;
import co.entomo.gdhcn.repository.QrCodeRepository;
import co.entomo.gdhcn.repository.RecipientKeyRepository;
import co.entomo.gdhcn.service.GdhcnFileSystem;
import co.entomo.gdhcn.service.GdhcnService;
import co.entomo.gdhcn.util.CertificateUtils;
import co.entomo.gdhcn.util.HttpClientUtils;
import co.entomo.gdhcn.vo.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import nl.minvws.encoding.Base45;
import org.apache.commons.compress.compressors.CompressorException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author Uday Matta
 * @organization entomo Labs
 */
@Slf4j
@Service
public class GdhcnServiceImpl implements GdhcnService {

    @Value("${gdhcn.baseUrl}")
    private String baseUrl;
    @Value("${tng.country}")
    private String countryCode;
    @Value("${tng.dsc.privateKey}")
    private String dscKeyPath;
    @Value("${ips.shlink.expiry}")
    private long ipsShLinkExpiry;
    @Value("${tng.dsc.privateKey.kid}")
    private String kidId;
    @Value("${recipient.keyDurationMinutes}")
    private int keyDurationMinutes;
    @Autowired
    private QrCodeRepository qrCodeRepository;
    @Autowired
    private IpsFileRepository ipsFileRepository;
    @Autowired
    private CertificateUtils certificateUtils;
    @Autowired
    private HttpClientUtils httpClientUtils;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GdhcnFileSystem gdhcnFileSystem;
    private ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String vshcIssuance(QrCodeRequest qrCodeRequest) throws GdhcnValidationException {
        try {
            byte[] key = generateRandomSequence();
            String uuid = UUID.randomUUID().toString();
            String manifestId = Base64.getUrlEncoder().encodeToString(generateRandomSequence());
            String fileName = uuid + ".json";
            String jsonUrl = gdhcnFileSystem.getPath(fileName);
            String shUrl = null;
            if (StringUtils.hasLength(qrCodeRequest.getPassCode())) {
                shUrl = baseUrl + "/v2/manifests/" + manifestId;
            } else {
                shUrl = baseUrl + "/v2/ips-json/" + manifestId;
            }
            SHLinkContent shLinkPayload = SHLinkContent.builder()
                    .url(shUrl)
                    .flag(qrCodeRequest.getPassCode() != null ? "P" : "U")
                    .label("GDHCN Validator")
                    .exp(qrCodeRequest.getExpiresOn() != null ? qrCodeRequest.getExpiresOn().getTime() : null)
                    .key(Base64.getUrlEncoder().encodeToString(key))
                    .build();

            QrCode qrCode = modelMapper.map(qrCodeRequest, QrCode.class);
            qrCode.setJsonUrl(jsonUrl);
            qrCode.setManifestId(manifestId);
            qrCode.setId(uuid);
            qrCode.setKey(Base64.getUrlEncoder().encodeToString(key));
            qrCode.setFlag(shLinkPayload.getFlag());
            qrCodeRepository.save(qrCode);
            gdhcnFileSystem.uploadJson(fileName, qrCodeRequest.getJsonContent());
            String shLinkConsent = "shlink://" + Base64.getEncoder()
                    .encodeToString(OBJECT_MAPPER.writeValueAsString(shLinkPayload).getBytes(StandardCharsets.UTF_8));

            long expiredInMillies = new Date(Long.MAX_VALUE).getTime() / 1000L;
            if (!ObjectUtils.isEmpty(shLinkPayload.getExp())) {
                expiredInMillies = shLinkPayload.getExp() / 1000L;
            }

            SmartHealthLink link = SmartHealthLink.builder().shLink(shLinkConsent).build();
            List<SmartHealthLink> list = new ArrayList<SmartHealthLink>();
            HealthCertificate hCert = HealthCertificate.builder().healthLinks(list).build();
            list.add(link);
            CertificatePayload payload = CertificatePayload.builder()
                    .iat(System.currentTimeMillis())
                    .iss(countryCode)
                    .healthCertificate(hCert)
                    .exp(expiredInMillies)
                    .build();

            String payLoadJson = OBJECT_MAPPER.writeValueAsString(payload);
            PrivateKey privateKey = certificateUtils.getPrivateKey(getPrivateDSCKeyContent(), countryCode);
            OneKey cborPrivateKey = new OneKey(null, privateKey);
            String encoded = new GreenCertificateEncoder(cborPrivateKey, kidId).encode(payLoadJson);
            return encoded;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (CoseException e) {
            e.printStackTrace();
        } catch (CompressorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ValidateCwtResponse vshcValidation(String qrCodeEncodedContent) {
        Map<Integer, StepStatus> response = getDefaultStatus();
        SHLinkContent shLinkContent = null;
        int step = 0;
        try {
            log.info("qrCodeEncodedContent : " + qrCodeEncodedContent);
            log.info("response : " + response);
            if (!ObjectUtils.isEmpty(qrCodeEncodedContent)) {
                qrCodeEncodedContent = qrCodeEncodedContent.substring(4);
                byte[] decodedBytes = Base45.getDecoder().decode(qrCodeEncodedContent);
                updateStatus(response, ++step, ValidationStatus.SUCCESS);
                byte[] coseBytes = GreenCertificateDecoder.getCoseBytes(decodedBytes);
                updateStatus(response, ++step, ValidationStatus.SUCCESS);
                Sign1Message msg = (Sign1Message) Message.DecodeFromBytes(coseBytes, MessageTag.Sign1);
                log.info("Sign1Message : " + msg.toString());
                updateStatus(response, ++step, ValidationStatus.SUCCESS);
                String json = GreenCertificateDecoder.getJsonString(msg.GetContent());
                log.info("Json : " + json);
                updateStatus(response, ++step, ValidationStatus.SUCCESS);
                CertificatePayload certificatePayLoad = OBJECT_MAPPER.readValue(json, CertificatePayload.class);
                updateStatus(response, ++step, ValidationStatus.SUCCESS);
                log.info("Protected Header: " + msg.getProtectedAttributes());
                String kid = msg.getProtectedAttributes().get(HeaderKeys.KID.AsCBOR()).ToObject(String.class);
                GdhcnCertificateVO gdhcnCertificateVO = httpClientUtils.getGdhcnCertificate(certificatePayLoad.getIss(),
                        kid);
                if (gdhcnCertificateVO == null)
                    throw new RuntimeException("Unable to Fetch GDHCN Certificate");
                updateStatus(response, ++step, ValidationStatus.SUCCESS);
                PublicKey publicKey = certificateUtils.getPublicKey(gdhcnCertificateVO.getCertificate());
                OneKey oneKey = new OneKey(publicKey, null);
                GreenCertificateDecoder decoder = new GreenCertificateDecoder(oneKey);
                boolean status = decoder.validate(msg);
                if (status)
                    updateStatus(response, ++step, ValidationStatus.SUCCESS);
                else
                    throw new RuntimeException("Signature Validation failed");

                String shLink = certificatePayLoad.getHealthCertificate().getHealthLinks().get(0).getShLink();
                updateStatus(response, ++step, ValidationStatus.SUCCESS);
                shLink = shLink.substring("shlink://".length());
                log.info("shLink: " + shLink);
                shLink = new String(Base64.getDecoder().decode(shLink));
                log.info("shLink: " + shLink);
                shLinkContent = OBJECT_MAPPER.readValue(shLink, SHLinkContent.class);

                if (shLinkContent.getExp() != null) {
                    Date expDate = new Date(shLinkContent.getExp());
                    Date currentDate = new Date(System.currentTimeMillis());
                    if (currentDate.after(expDate)) {
                        shLinkContent = null;
                        throw new RuntimeException("shlink expired");
                    }
                }
                updateStatus(response, ++step, ValidationStatus.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            updateStatus(response, ++step, ValidationStatus.FAILED);
        }
        return ValidateCwtResponse.builder()
                .validationStatus(response)
                .shLinkContent(shLinkContent)
                .build();
    }

    private byte[] generateRandomSequence() {
        byte[] randomSequence = new byte[32];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomSequence);
        return randomSequence;
    }

    private Map<Integer, StepStatus> getDefaultStatus() {
        Map<Integer, StepStatus> map = new HashMap<Integer, StepStatus>();
        map.put(1, new StepStatus("1", ValidationStatus.PENDING, ValidationDescription.DECODE_BASE45));
        map.put(2, new StepStatus("2", ValidationStatus.PENDING, ValidationDescription.DEFLATE_COSE_BYTES));
        map.put(3, new StepStatus("3", ValidationStatus.PENDING, ValidationDescription.CONVERT_COSE_MESSAGE));
        map.put(4, new StepStatus("4", ValidationStatus.PENDING, ValidationDescription.COSE_MESSAGE_PAYLOAD_TO_JSON));
        map.put(5, new StepStatus("5", ValidationStatus.PENDING, ValidationDescription.EXTRACT_COUNTRY_CODE));
        map.put(6, new StepStatus("6", ValidationStatus.PENDING, ValidationDescription.FETCH_PUBLIC_KEY_GDHCN));
        map.put(7, new StepStatus("7", ValidationStatus.PENDING, ValidationDescription.VALIDATE_SIGNATURE));
        map.put(8, new StepStatus("8", ValidationStatus.PENDING, ValidationDescription.EXTRACT_HCERT));
        map.put(9, new StepStatus("9", ValidationStatus.PENDING, ValidationDescription.VALIDATE_EXPIRY));
        return map;

    }

    private void updateStatus(Map<Integer, StepStatus> response, Integer step, ValidationStatus status) {
        response.get(step).setStatus(status);
        if (status == ValidationStatus.FAILED)
            response.get(step).setError(response.get(step).getCode().getErrorMessage());
    }

    @Override
    public ManifestResponse getManifest(ManifestRequest manifestRequest, String manifestId)
            throws GdhcnValidationException {
        ManifestResponse response = new ManifestResponse();
        QrCode qrCode = qrCodeRepository.findByManifestId(manifestId).get();
        if (qrCode != null) {
            if (qrCode.getFlag() == null || (qrCode.getFlag() != null && !qrCode.getFlag().contains("P"))) {
                throw new RuntimeException();
            }
            if (!qrCode.getPassCode().contentEquals(manifestRequest.getPasscode())) {
                throw new GdhcnValidationException("");
            }
            // Ips Created.
            Optional<IpsFile> optIpsFile = ipsFileRepository.findByManifestId(manifestId);
            IpsFile ipsFile = null;
            if (optIpsFile.isEmpty()) {
                ipsFile = new IpsFile();
                ipsFile.setManifestId(manifestId);
                ipsFile = ipsFileRepository.save(ipsFile);
            } else {
                ipsFile = optIpsFile.get();
                if (ipsFile.getCreatedAt().toInstant().plus(ipsShLinkExpiry, ChronoUnit.MINUTES).isBefore(Instant.now())
                        || ipsFile.isAccessed()) {
                    ipsFileRepository.delete(ipsFile);
                    ipsFile = new IpsFile();
                    ipsFile.setManifestId(manifestId);
                    ipsFile = ipsFileRepository.save(ipsFile);
                }
            }

            String url = baseUrl + "/v2/ips-json/" + ipsFile.getId();
            response.put("files", List.of(Map.of("contentType", "application/fhir+json", "location", url)));
            return response;
        }
        throw new GdhcnValidationException("Invalid request");
    }

    @Override
    public String downloadJson(String manifestId) throws GdhcnValidationException {
        try {
            // Incase flag not contains P - direct access
            Optional<QrCode> optionalQrCode = qrCodeRepository.findByManifestId(manifestId);
            if (optionalQrCode.isPresent()) {
                QrCode qrCode = optionalQrCode.get();
                if (qrCode != null && qrCode.getFlag() != null && qrCode.getFlag().contains("P")) {
                    throw new GdhcnValidationException("Invalid request");
                }
                if (qrCode != null) {
                    String fileName = qrCode.getId() + ".json";
                    InputStream is = gdhcnFileSystem.downloadJson(fileName);
                    byte[] rawContent = is.readAllBytes();
                    String jsonContent = new String(rawContent);
                    log.info("Downloaded json: " + jsonContent);
                    qrCodeRepository.save(qrCode);
                    return jsonContent;
                }
            }
            // access through manifest
            IpsFile ipsFile = ipsFileRepository.findById(manifestId).get();
            if (ipsFile != null && ipsFile.isAccessed()) {
                throw new GdhcnIPSAlreadyAccessedException("Already Accessed");
            }
            ipsFile.setAccessed(true);
            QrCode qrCode = qrCodeRepository.findByManifestId(ipsFile.getManifestId()).get();
            if (qrCode != null) {
                String fileName = qrCode.getId() + ".json";
                InputStream is = gdhcnFileSystem.downloadJson(fileName);
                byte[] rawContent = is.readAllBytes();
                String jsonContent = new String(rawContent);
                log.info("Downloaded json: " + jsonContent);
                ipsFileRepository.save(ipsFile);
                return jsonContent;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public String downloadJWEJson(String jsonId) {
        try {
            QrCode qrCode = qrCodeRepository.findById(jsonId).get();
            if (qrCode != null) {
                String fileName = jsonId + ".json";
                InputStream is = gdhcnFileSystem.downloadJson(fileName);
                byte[] rawContent = is.readAllBytes();
                String jsonContent = new String(rawContent);
                log.info("Downloaded json: " + jsonContent);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private String getPrivateDSCKeyContent() throws IOException {
        StringBuilder content = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(dscKeyPath);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader reader = new BufferedReader(isr)) {

            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }
        return content.toString();
    }
}
