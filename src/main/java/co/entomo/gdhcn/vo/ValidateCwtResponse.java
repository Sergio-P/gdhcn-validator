package co.entomo.gdhcn.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Uday Matta
 * @organization entomo Labs
 */
@Data
@Builder
public class ValidateCwtResponse {
    @Schema(name = "validationStatus", example = "{\"1\":{\"step\":\"1\",\"status\":\"SUCCESS\",\"code\":\"DECODE_BASE45\",\"description\":\"Decoding Base45 QR\",\"error\":null},\"2\":{\"step\":\"2\",\"status\":\"SUCCESS\",\"code\":\"DEFLATE_COSE_BYTES\",\"description\":\"Decompressing (Deflate) decoded QR Payload\",\"error\":null},\"3\":{\"step\":\"3\",\"status\":\"SUCCESS\",\"code\":\"CONVERT_COSE_MESSAGE\",\"description\":\"Converting Decompressed Payload to CWT\",\"error\":null},\"4\":{\"step\":\"4\",\"status\":\"SUCCESS\",\"code\":\"COSE_MESSAGE_PAYLOAD_TO_JSON\",\"description\":\"Extracting Claims from CWT\",\"error\":null},\"5\":{\"step\":\"5\",\"status\":\"SUCCESS\",\"code\":\"EXTRACT_COUNTRY_CODE\",\"description\":\"Extracting Country Code\",\"error\":null},\"6\":{\"step\":\"6\",\"status\":\"SUCCESS\",\"code\":\"FETCH_PUBLIC_KEY_GDHCN\",\"description\":\"Connecting & Fetching Public Key from GDHCN\",\"error\":null},\"7\":{\"step\":\"7\",\"status\":\"SUCCESS\",\"code\":\"VALIDATE_SIGNATURE\",\"description\":\"Validating Signature\",\"error\":null},\"8\":{\"step\":\"8\",\"status\":\"SUCCESS\",\"code\":\"EXTRACT_HCERT\",\"description\":\"Extracting Smart Health Link\",\"error\":null},\"9\":{\"step\":\"9\",\"status\":\"SUCCESS\",\"code\":\"VALIDATE_EXPIRY\",\"description\":\"Verifying SHL QR Expiry\",\"error\":null}}")
    private Map<Integer, StepStatus> validationStatus;
    @Schema(name = "shLinkContent", example = "{\"url\":\"http://example-url.com/v2/ips-json/id\",\"flag\":\"U\",\"exp\":1745589915953,\"key\":\"key-string\",\"label\":\"GDHCN Validator\"}")
    private SHLinkContent shLinkContent;
}
