package co.entomo.gdhcn.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import co.entomo.gdhcn.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.entomo.gdhcn.exceptions.GdhcnValidationException;
import co.entomo.gdhcn.service.GdhcnService;
import lombok.AllArgsConstructor;

/**
 * @author Uday Matta
 * @organization Entomo Labs
 *               REST controller for handling GDHCN-related requests.
 *               This controller provides endpoints for issuing and validating
 *               VSHC (Vaccination Status Health Certificate),
 *               retrieving IPS JSON data, and getting manifests.
 */
@RestController
@AllArgsConstructor
public class GdhcnController {
	private final GdhcnService gdhcnService;

	/**
	 * Endpoint for issuing a VSHC (Vaccination Status Health Certificate).
	 *
	 * @param qrCodeRequest the request body containing the QR code data.
	 * @return a {@link ResponseEntity} containing the issued VSHC in JSON format.
	 * @throws GdhcnValidationException if there is a validation error.
	 */
	@Operation(summary = "Issues a QR code based on the provided JSON.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "QR Code created", content = {
					@Content(mediaType = "application/json", schema = @Schema(type = "string", example = "HC1:6BFOXNMG2N9H1/4W%MXN5PR7QO5SYP60JO DJS4//O:R8LCDO%0TJH/U3HT70%45C3SWQK.4W$4MJVIFT/FJ0R5BS6YVBS7BGJP49B7IIYIJY%D49M8MB/$CMYJ14KW9K8-I3QS7XA*6K6EAG.ST1J*.TP.AWUBIOJ3XJP1JGQSQHB9ED+0EUEU2ND5TJJCD6ZO6JPW1O3HH2:FVD9P*2$.F7/B*GI 85H1GCY0*2OZGIK%7%GGZGIU:J+JI%2M85O$$0*2O2.DW5WPNP:9WY1M FADIID.7*/BI582SUVXMLO6-KG5RHPSERU9N*J:/PIO8PY1.G8T%95P8AP5JJ8XV16O5PK6K-H S9BQT*UM0*V6RE*NGPY7GYHG H*VH1+HBJ91%BKL5GSMCTLC%B9PKEN1ZU1 :R4P1KTM4P1D$UCU9CXBGE5%0SZ%PHMLP982V220HP/S/A0*JUB-9QV184VXCVBUH6793CQ.0IK11BQ92.U.XHAFNAC3WY54OSCANN:47HHR$I*XB+NMBETJW3 D90SUG%DZ4BLVBSFVII2%5Q5RNCGD XQ0M8W/6NV9HYKL28/LL2MKT/M19Q4ID:RLM50L:EG4")) }),
	})
	@PostMapping(value = "/v2/vshcIssuance", produces = { "application/json" })
	public ResponseEntity<String> vshcIssuance(@RequestBody QrCodeRequest qrCodeRequest) throws GdhcnValidationException {
		String cwt = gdhcnService.vshcIssuance(qrCodeRequest);
		return ResponseEntity.of(Optional.of(cwt));
	}

	/**
	 * Endpoint for validating a VSHC.
	 *
	 * @param body the request body containing the QR code content.
	 * @return a {@link ResponseEntity} containing the validation response in FHIR
	 *         JSON format.
	 * @throws GdhcnValidationException if there is a validation error.
	 */
	@Operation(summary = "Validates a QR code.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "QR Validated successfully", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ValidateCwtResponse.class)) }),
	})
	@PostMapping(value = "/v2/vshcValidation", produces = { "application/fhir+json" })
	public ResponseEntity<ValidateCwtResponse> vshcValidation(@Valid @RequestBody ValidateRequest body)
			throws GdhcnValidationException {
		ValidateCwtResponse status = gdhcnService.vshcValidation(body.getQrCodeContent());
		return ResponseEntity.of(Optional.of(status));
	}

	/**
	 * Endpoint for retrieving IPS JSON data.
	 *
	 * @param manifestId the ID of the JSON data to retrieve.
	 * @return a {@link ResponseEntity} containing the JSON content.
	 */
	@Operation(summary = "Endpoint for retrieving IPS JSON data.", description = "Please request a new URL when it is not longer accessible (Last around 5 min).")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "IPS JSON data retrieved successfully", content = {
					@Content(mediaType = "application/json", schema = @Schema(type = "string", example = "{\"resourceType\":\"Bundle\",\"id\":\"bundle-minimal\",\"language\":\"en-US\",\"identifier\":{\"system\":\"urn:oid:2.16.724.4.8.10.200.10\",\"value\":\"28b95815-76ce-457b-b7ae-a972e527db40\"},\"type\":\"document\",\"timestamp\":\"2020-12-11T14:30:00+01:00\",\"entry\":[{\"fullUrl\":\"urn:uuid:f40b07e3-37e8-48c3-bf1c-ae70fe12dab0\",\"resource\":{\"resourceType\":\"Composition\",\"id\":\"f40b07e3-37e8-48c3-bf1c-ae70fe12dab0\",\"text\":{\"status\":\"generated\",\"div\":\"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><a name=\\\"Composition_f40b07e3-37e8-48c3-bf1c-ae70fe12dab0\\\"> </a><p class=\\\"res-header-id\\\"><b>Generated Narrative: Composition f40b07e3-37e8-48c3-bf1c-ae70fe12dab0</b></p><a name=\\\"f40b07e3-37e8-48c3-bf1c-ae70fe12dab0\\\"> </a><a name=\\\"hcf40b07e3-37e8-48c3-bf1c-ae70fe12dab0\\\"> </a><a name=\\\"f40b07e3-37e8-48c3-bf1c-ae70fe12dab0-en-US\\\"> </a><p><b>status</b>: Final</p><p><b>type</b>: <span title=\\\"Codes:{http://loinc.org 60591-5}\\\">Patient summary Document</span></p><p><b>date</b>: 2020-12-11 14:30:00+0100</p><p><b>author</b>: <a href=\\\"Bundle-bundle-minimal.html#urn-uuid-45271f7f-63ab-4946-970f-3daaaa06637f\\\">Bundle: identifier = urn:oid:2.16.724.4.8.10.200.10#28b95815-76ce-457b-b7ae-a972e527db40; type = document; timestamp = 2020-12-11 14:30:00+0100</a></p><p><b>title</b>: Patient Summary as of December 11, 2020 14:30</p><p><b>confidentiality</b>: normal</p><blockquote><p><b>attester</b></p><p><b>mode</b>: Legal</p><p><b>time</b>: 2020-12-11 14:30:00+0100</p><p><b>party</b>: <a href=\\\"Bundle-bundle-minimal.html#urn-uuid-45271f7f-63ab-4946-970f-3daaaa06637f\\\">Bundle: identifier = urn:oid:2.16.724.4.8.10.200.10#28b95815-76ce-457b-b7ae-a972e527db40; type = document; timestamp = 2020-12-11 14:30:00+0100</a></p></blockquote><blockquote><p><b>attester</b></p><p><b>mode</b>: Legal</p><p><b>time</b>: 2020-12-11 14:30:00+0100</p><p><b>party</b>: <a href=\\\"Bundle-bundle-minimal.html#urn-uuid-a21fe796-3594-4ad9-a01e-cc83118ceca9\\\">Bundle: identifier = urn:oid:2.16.724.4.8.10.200.10#28b95815-76ce-457b-b7ae-a972e527db40; type = document; timestamp = 2020-12-11 14:30:00+0100</a></p></blockquote><p><b>custodian</b>: <a href=\\\"Bundle-bundle-minimal.html#urn-uuid-a21fe796-3594-4ad9-a01e-cc83118ceca9\\\">Bundle: identifier = urn:oid:2.16.724.4.8.10.200.10#28b95815-76ce-457b-b7ae-a972e527db40; type = document; timestamp = 2020-12-11 14:30:00+0100</a></p><h3>RelatesTos</h3><table class=\\\"grid\\\"><tr><td style=\\\"display: none\\\">-</td><td><b>Code</b></td><td><b>Target[x]</b></td></tr><tr><td style=\\\"display: none\\\">*</td><td>Appends</td><td><code>urn:oid:2.16.724.4.8.10.200.10</code>/20e12ce3-857f-49c0-b888-cb670597f191</td></tr></table><h3>Events</h3><table class=\\\"grid\\\"><tr><td style=\\\"display: none\\\">-</td><td><b>Code</b></td><td><b>Period</b></td></tr><tr><td style=\\\"display: none\\\">*</td><td><span title=\\\"Codes:{http://terminology.hl7.org/CodeSystem/v3-ActClass PCPR}\\\">care provision</span></td><td>?? --&gt; 2020-12-11 14:30:00+0100</td></tr></table></div>\"},\"status\":\"final\",\"type\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"60591-5\",\"display\":\"Patient summary Document\"}]},\"subject\":{\"reference\":\"urn:uuid:244ad7c3-beeb-41d1-8a2f-c76b8cf720ad\"},\"date\":\"2020-12-11T14:30:00+01:00\",\"author\":[{\"reference\":\"urn:uuid:45271f7f-63ab-4946-970f-3daaaa06637f\"}],\"title\":\"Patient Summary as of December 11, 2020 14:30\",\"confidentiality\":\"N\",\"attester\":[{\"mode\":\"legal\",\"time\":\"2020-12-11T14:30:00+01:00\",\"party\":{\"reference\":\"urn:uuid:45271f7f-63ab-4946-970f-3daaaa06637f\"}},{\"mode\":\"legal\",\"time\":\"2020-12-11T14:30:00+01:00\",\"party\":{\"reference\":\"urn:uuid:a21fe796-3594-4ad9-a01e-cc83118ceca9\"}}],\"custodian\":{\"reference\":\"urn:uuid:a21fe796-3594-4ad9-a01e-cc83118ceca9\"},\"relatesTo\":[{\"code\":\"appends\",\"targetIdentifier\":{\"system\":\"urn:oid:2.16.724.4.8.10.200.10\",\"value\":\"20e12ce3-857f-49c0-b888-cb670597f191\"}}],\"event\":[{\"code\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/v3-ActClass\",\"code\":\"PCPR\"}]}],\"period\":{\"end\":\"2020-12-11T14:30:00+01:00\"}}],\"section\":[{\"title\":\"Active Problems\",\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"11450-4\",\"display\":\"Problem list - Reported\"}]},\"text\":{\"status\":\"generated\",\"div\":\"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><ul><li><div><b>Condition Name</b>: Menopausal Flushing</div><div><b>Code</b>: <span>198436008</span></div><div><b>Status</b>: <span>Active</span></div></li></ul></div>\"},\"entry\":[{\"reference\":\"urn:uuid:d179321e-c091-4cd4-8642-3a27537d506d\"}]},{\"title\":\"Medication\",\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"10160-0\",\"display\":\"History of Medication use Narrative\"}]},\"text\":{\"status\":\"generated\",\"div\":\"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><ul><li><div><b>Medication Name</b>: Oral anastrozole 1mg tablet</div><div><b>Code</b>: <span></span></div><div><b>Status</b>: <span>Active, started March 2015</span></div><div>Instructions: Take 1 time per day</div></li></ul></div>\"},\"entry\":[{\"reference\":\"urn:uuid:e1271efd-18ff-4654-9ee7-45f40019c453\"}]},{\"title\":\"Allergies and Intolerances\",\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"48765-2\",\"display\":\"Allergies and adverse reactions Document\"}]},\"text\":{\"status\":\"generated\",\"div\":\"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><ul><li><div><b>Allergy Name</b>: Pencillins</div><div><b>Verification Status</b>: Confirmed</div><div><b>Reaction</b>: <span>no information</span></div></li></ul></div>\"},\"entry\":[{\"reference\":\"urn:uuid:74861316-f69d-4652-9fb1-8512a20c7927\"}]}]}},{\"fullUrl\":\"urn:uuid:244ad7c3-beeb-41d1-8a2f-c76b8cf720ad\",\"resource\":{\"resourceType\":\"Patient\",\"id\":\"244ad7c3-beeb-41d1-8a2f-c76b8cf720ad\",\"text\":{\"status\":\"generated\",\"div\":\"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><a name=\\\"Patient_244ad7c3-beeb-41d1-8a2f-c76b8cf720ad\\\"> </a><p class=\\\"res-header-id\\\"><b>Generated Narrative: Patient 244ad7c3-beeb-41d1-8a2f-c76b8cf720ad</b></p><a name=\\\"244ad7c3-beeb-41d1-8a2f-c76b8cf720ad\\\"> </a><a name=\\\"hc244ad7c3-beeb-41d1-8a2f-c76b8cf720ad\\\"> </a><a name=\\\"244ad7c3-beeb-41d1-8a2f-c76b8cf720ad-en-US\\\"> </a><p style=\\\"border: 1px #661aff solid; background-color: #e6e6ff; padding: 10px;\\\">Martha DeLarosa  Female, DoB: 1972-05-01 ( urn:oid:2.16.840.1.113883.2.4.6.3#574687583)</p><hr/><table class=\\\"grid\\\"><tr><td style=\\\"background-color: #f3f5da\\\" title=\\\"Record is active\\\">Active:</td><td colspan=\\\"3\\\">true</td></tr><tr><td style=\\\"background-color: #f3f5da\\\" title=\\\"Ways to contact the Patient\\\">Contact Detail</td><td colspan=\\\"3\\\"><ul><li><a href=\\\"tel:+31788700800\\\">+31788700800</a></li><li>Laan Van Europa 1600 Dordrecht 3317 DB NL </li></ul></td></tr><tr><td style=\\\"background-color: #f3f5da\\\" title=\\\"Nominated Contact: mother\\\">mother:</td><td colspan=\\\"3\\\"><ul><li>Martha Mum </li><li>Promenade des Anglais 111 Lyon 69001 FR </li><li><a href=\\\"tel:+33-555-20036\\\">+33-555-20036</a></li></ul></td></tr></table></div>\"},\"identifier\":[{\"system\":\"urn:oid:2.16.840.1.113883.2.4.6.3\",\"value\":\"574687583\"}],\"active\":true,\"name\":[{\"family\":\"DeLarosa\",\"given\":[\"Martha\"]}],\"telecom\":[{\"system\":\"phone\",\"value\":\"+31788700800\",\"use\":\"home\"}],\"gender\":\"female\",\"birthDate\":\"1972-05-01\",\"address\":[{\"line\":[\"Laan Van Europa 1600\"],\"city\":\"Dordrecht\",\"postalCode\":\"3317 DB\",\"country\":\"NL\"}],\"contact\":[{\"relationship\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/v3-RoleCode\",\"code\":\"MTH\"}]}],\"name\":{\"family\":\"Mum\",\"given\":[\"Martha\"]},\"telecom\":[{\"system\":\"phone\",\"value\":\"+33-555-20036\",\"use\":\"home\"}],\"address\":{\"line\":[\"Promenade des Anglais 111\"],\"city\":\"Lyon\",\"postalCode\":\"69001\",\"country\":\"FR\"}}]}},{\"fullUrl\":\"urn:uuid:45271f7f-63ab-4946-970f-3daaaa06637f\",\"resource\":{\"resourceType\":\"Practitioner\",\"id\":\"45271f7f-63ab-4946-970f-3daaaa06637f\",\"text\":{\"status\":\"generated\",\"div\":\"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><a name=\\\"Practitioner_45271f7f-63ab-4946-970f-3daaaa06637f\\\"> </a><p class=\\\"res-header-id\\\"><b>Generated Narrative: Practitioner 45271f7f-63ab-4946-970f-3daaaa06637f</b></p><a name=\\\"45271f7f-63ab-4946-970f-3daaaa06637f\\\"> </a><a name=\\\"hc45271f7f-63ab-4946-970f-3daaaa06637f\\\"> </a><a name=\\\"45271f7f-63ab-4946-970f-3daaaa06637f-en-US\\\"> </a><p><b>identifier</b>: <code>urn:oid:2.16.528.1.1007.3.1</code>/129854633</p><p><b>active</b>: true</p><p><b>name</b>: Beetje van Hulp </p><h3>Qualifications</h3><table class=\\\"grid\\\"><tr><td style=\\\"display: none\\\">-</td><td><b>Code</b></td></tr><tr><td style=\\\"display: none\\\">*</td><td><span title=\\\"Codes:{http://terminology.hl7.org/CodeSystem/v2-0360 MD}\\\">Doctor of Medicine</span></td></tr></table></div>\"},\"identifier\":[{\"system\":\"urn:oid:2.16.528.1.1007.3.1\",\"value\":\"129854633\",\"assigner\":{\"display\":\"CIBG\"}}],\"active\":true,\"name\":[{\"family\":\"van Hulp\",\"given\":[\"Beetje\"]}],\"qualification\":[{\"code\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/v2-0360\",\"version\":\"2.7\",\"code\":\"MD\",\"display\":\"Doctor of Medicine\"}]}}]}},{\"fullUrl\":\"urn:uuid:a21fe796-3594-4ad9-a01e-cc83118ceca9\",\"resource\":{\"resourceType\":\"Organization\",\"id\":\"a21fe796-3594-4ad9-a01e-cc83118ceca9\",\"text\":{\"status\":\"generated\",\"div\":\"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><a name=\\\"Organization_a21fe796-3594-4ad9-a01e-cc83118ceca9\\\"> </a><p class=\\\"res-header-id\\\"><b>Generated Narrative: Organization a21fe796-3594-4ad9-a01e-cc83118ceca9</b></p><a name=\\\"a21fe796-3594-4ad9-a01e-cc83118ceca9\\\"> </a><a name=\\\"hca21fe796-3594-4ad9-a01e-cc83118ceca9\\\"> </a><a name=\\\"a21fe796-3594-4ad9-a01e-cc83118ceca9-en-US\\\"> </a><p><b>identifier</b>: <code>urn:oid:2.16.528.1.1007.3.3</code>/564738757</p><p><b>active</b>: true</p><p><b>name</b>: Anorg Aniza Tion BV / The best custodian ever</p><p><b>telecom</b>: <a href=\\\"tel:+31-51-34343400\\\">+31-51-34343400</a></p><p><b>address</b>: Houttuinen 27 Dordrecht 3311 CE NL (work)</p></div>\"},\"identifier\":[{\"system\":\"urn:oid:2.16.528.1.1007.3.3\",\"value\":\"564738757\"}],\"active\":true,\"name\":\"Anorg Aniza Tion BV / The best custodian ever\",\"telecom\":[{\"system\":\"phone\",\"value\":\"+31-51-34343400\",\"use\":\"work\"}],\"address\":[{\"use\":\"work\",\"line\":[\"Houttuinen 27\"],\"city\":\"Dordrecht\",\"postalCode\":\"3311 CE\",\"country\":\"NL\"}]}},{\"fullUrl\":\"urn:uuid:d179321e-c091-4cd4-8642-3a27537d506d\",\"resource\":{\"resourceType\":\"Condition\",\"id\":\"d179321e-c091-4cd4-8642-3a27537d506d\",\"text\":{\"status\":\"generated\",\"div\":\"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><a name=\\\"Condition_d179321e-c091-4cd4-8642-3a27537d506d\\\"> </a><p class=\\\"res-header-id\\\"><b>Generated Narrative: Condition d179321e-c091-4cd4-8642-3a27537d506d</b></p><a name=\\\"d179321e-c091-4cd4-8642-3a27537d506d\\\"> </a><a name=\\\"hcd179321e-c091-4cd4-8642-3a27537d506d\\\"> </a><a name=\\\"d179321e-c091-4cd4-8642-3a27537d506d-en-US\\\"> </a><p><b>identifier</b>: <code>urn:oid:1.2.3.999</code>/cacceb57-395f-48e1-9c88-e9c9704dc2d2</p><p><b>clinicalStatus</b>: <span title=\\\"Codes:{http://terminology.hl7.org/CodeSystem/condition-clinical active}\\\">Active</span></p><p><b>verificationStatus</b>: <span title=\\\"Codes:{http://terminology.hl7.org/CodeSystem/condition-ver-status confirmed}\\\">Confirmed</span></p><p><b>category</b>: <span title=\\\"Codes:{http://loinc.org 75326-9}\\\">Problem</span></p><p><b>severity</b>: <span title=\\\"Codes:{http://loinc.org LA6751-7}\\\">Moderate</span></p><p><b>code</b>: <span title=\\\"Codes:{http://snomed.info/sct 198436008}, {http://hl7.org/fhir/sid/icd-10 N95.1}\\\">Menopausal flushing (finding)</span></p><p><b>subject</b>: <a href=\\\"Bundle-bundle-minimal.html#urn-uuid-244ad7c3-beeb-41d1-8a2f-c76b8cf720ad\\\">Bundle: identifier = urn:oid:2.16.724.4.8.10.200.10#28b95815-76ce-457b-b7ae-a972e527db40; type = document; timestamp = 2020-12-11 14:30:00+0100</a></p><p><b>onset</b>: 2015</p><p><b>recordedDate</b>: 2016-10</p></div>\"},\"identifier\":[{\"system\":\"urn:oid:1.2.3.999\",\"value\":\"cacceb57-395f-48e1-9c88-e9c9704dc2d2\"}],\"clinicalStatus\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-clinical\",\"code\":\"active\"}]},\"verificationStatus\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-ver-status\",\"code\":\"confirmed\"}]},\"category\":[{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"75326-9\",\"display\":\"Problem\"}]}],\"severity\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"LA6751-7\",\"display\":\"Moderate\"}]},\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"198436008\",\"display\":\"Menopausal flushing (finding)\",\"_display\":{\"extension\":[{\"extension\":[{\"url\":\"lang\",\"valueCode\":\"nl-NL\"},{\"url\":\"content\",\"valueString\":\"opvliegers\"}],\"url\":\"http://hl7.org/fhir/StructureDefinition/translation\"}]}},{\"system\":\"http://hl7.org/fhir/sid/icd-10\",\"code\":\"N95.1\",\"display\":\"Menopausal and female climacteric states\"}]},\"subject\":{\"reference\":\"urn:uuid:244ad7c3-beeb-41d1-8a2f-c76b8cf720ad\"},\"onsetDateTime\":\"2015\",\"recordedDate\":\"2016-10\"}},{\"fullUrl\":\"urn:uuid:e1271efd-18ff-4654-9ee7-45f40019c453\",\"resource\":{\"resourceType\":\"MedicationStatement\",\"id\":\"e1271efd-18ff-4654-9ee7-45f40019c453\",\"text\":{\"status\":\"generated\",\"div\":\"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><a name=\\\"MedicationStatement_e1271efd-18ff-4654-9ee7-45f40019c453\\\"> </a><p class=\\\"res-header-id\\\"><b>Generated Narrative: MedicationStatement e1271efd-18ff-4654-9ee7-45f40019c453</b></p><a name=\\\"e1271efd-18ff-4654-9ee7-45f40019c453\\\"> </a><a name=\\\"hce1271efd-18ff-4654-9ee7-45f40019c453\\\"> </a><a name=\\\"e1271efd-18ff-4654-9ee7-45f40019c453-en-US\\\"> </a><p><b>identifier</b>: <code>urn:oid:1.2.3.999</code>/8faf0319-89d3-427c-b9d1-e8c8fd390dca</p><p><b>status</b>: Active</p><p><b>medication</b>: <a href=\\\"Bundle-bundle-minimal.html#urn-uuid-95db7c92-566a-4ded-896b-2220ab244a9e\\\">Bundle: identifier = urn:oid:2.16.724.4.8.10.200.10#28b95815-76ce-457b-b7ae-a972e527db40; type = document; timestamp = 2020-12-11 14:30:00+0100</a></p><p><b>subject</b>: <a href=\\\"Bundle-bundle-minimal.html#urn-uuid-244ad7c3-beeb-41d1-8a2f-c76b8cf720ad\\\">Bundle: identifier = urn:oid:2.16.724.4.8.10.200.10#28b95815-76ce-457b-b7ae-a972e527db40; type = document; timestamp = 2020-12-11 14:30:00+0100</a></p><p><b>effective</b>: 2015-03 --&gt; (ongoing)</p><blockquote><p><b>dosage</b></p></blockquote></div>\"},\"identifier\":[{\"system\":\"urn:oid:1.2.3.999\",\"value\":\"8faf0319-89d3-427c-b9d1-e8c8fd390dca\"}],\"status\":\"active\",\"medicationReference\":{\"reference\":\"urn:uuid:95db7c92-566a-4ded-896b-2220ab244a9e\"},\"subject\":{\"reference\":\"urn:uuid:244ad7c3-beeb-41d1-8a2f-c76b8cf720ad\"},\"effectivePeriod\":{\"start\":\"2015-03\"},\"dosage\":[{\"timing\":{\"repeat\":{\"count\":1,\"periodUnit\":\"d\"}},\"route\":{\"coding\":[{\"system\":\"http://standardterms.edqm.eu\",\"code\":\"20053000\",\"display\":\"Oral use\"}]},\"doseAndRate\":[{\"doseQuantity\":{\"value\":1,\"unit\":\"tablet\",\"system\":\"http://unitsofmeasure.org\",\"code\":\"1\"}}]}]}},{\"fullUrl\":\"urn:uuid:95db7c92-566a-4ded-896b-2220ab244a9e\",\"resource\":{\"resourceType\":\"Medication\",\"id\":\"95db7c92-566a-4ded-896b-2220ab244a9e\",\"text\":{\"status\":\"generated\",\"div\":\"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><a name=\\\"Medication_95db7c92-566a-4ded-896b-2220ab244a9e\\\"> </a><p class=\\\"res-header-id\\\"><b>Generated Narrative: Medication 95db7c92-566a-4ded-896b-2220ab244a9e</b></p><a name=\\\"95db7c92-566a-4ded-896b-2220ab244a9e\\\"> </a><a name=\\\"hc95db7c92-566a-4ded-896b-2220ab244a9e\\\"> </a><a name=\\\"95db7c92-566a-4ded-896b-2220ab244a9e-en-US\\\"> </a><p><b>code</b>: <span title=\\\"Codes:{http://snomed.info/sct 108774000}, {urn:oid:2.16.840.1.113883.2.4.4.1 99872}, {urn:oid:2.16.840.1.113883.2.4.4.7 2076667}, {http://www.whocc.no/atc L02BG03}\\\">Product containing anastrozole (medicinal product)</span></p></div>\"},\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"108774000\",\"display\":\"Product containing anastrozole (medicinal product)\"},{\"system\":\"urn:oid:2.16.840.1.113883.2.4.4.1\",\"code\":\"99872\",\"display\":\"ANASTROZOL 1MG TABLET\"},{\"system\":\"urn:oid:2.16.840.1.113883.2.4.4.7\",\"code\":\"2076667\",\"display\":\"ANASTROZOL CF TABLET FILMOMHULD 1MG\"},{\"system\":\"http://www.whocc.no/atc\",\"code\":\"L02BG03\",\"display\":\"anastrozole\"}]}}},{\"fullUrl\":\"urn:uuid:74861316-f69d-4652-9fb1-8512a20c7927\",\"resource\":{\"resourceType\":\"AllergyIntolerance\",\"id\":\"74861316-f69d-4652-9fb1-8512a20c7927\",\"text\":{\"status\":\"generated\",\"div\":\"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\"><a name=\\\"AllergyIntolerance_74861316-f69d-4652-9fb1-8512a20c7927\\\"> </a><p class=\\\"res-header-id\\\"><b>Generated Narrative: AllergyIntolerance 74861316-f69d-4652-9fb1-8512a20c7927</b></p><a name=\\\"74861316-f69d-4652-9fb1-8512a20c7927\\\"> </a><a name=\\\"hc74861316-f69d-4652-9fb1-8512a20c7927\\\"> </a><a name=\\\"74861316-f69d-4652-9fb1-8512a20c7927-en-US\\\"> </a><p><b>identifier</b>: <code>urn:oid:1.2.3.999</code>/8d9566a4-d26d-46be-a3e4-c9f3a0e5cd83</p><p><b>clinicalStatus</b>: <span title=\\\"Codes:{http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical active}\\\">Active</span></p><p><b>verificationStatus</b>: <span title=\\\"Codes:{http://terminology.hl7.org/CodeSystem/allergyintolerance-verification confirmed}\\\">Confirmed</span></p><p><b>type</b>: Allergy</p><p><b>category</b>: Medication</p><p><b>criticality</b>: High Risk</p><p><b>code</b>: <span title=\\\"Codes:{http://snomed.info/sct 373270004}\\\">Substance with penicillin structure and antibacterial mechanism of action (substance)</span></p><p><b>patient</b>: <a href=\\\"Bundle-bundle-minimal.html#urn-uuid-244ad7c3-beeb-41d1-8a2f-c76b8cf720ad\\\">Bundle: identifier = urn:oid:2.16.724.4.8.10.200.10#28b95815-76ce-457b-b7ae-a972e527db40; type = document; timestamp = 2020-12-11 14:30:00+0100</a></p><p><b>onset</b>: 2010</p></div>\"},\"identifier\":[{\"system\":\"urn:oid:1.2.3.999\",\"value\":\"8d9566a4-d26d-46be-a3e4-c9f3a0e5cd83\"}],\"clinicalStatus\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical\",\"code\":\"active\"}]},\"verificationStatus\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/allergyintolerance-verification\",\"code\":\"confirmed\"}]},\"type\":\"allergy\",\"category\":[\"medication\"],\"criticality\":\"high\",\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"373270004\",\"display\":\"Substance with penicillin structure and antibacterial mechanism of action (substance)\"}]},\"patient\":{\"reference\":\"urn:uuid:244ad7c3-beeb-41d1-8a2f-c76b8cf720ad\"},\"onsetDateTime\":\"2010\"}}]}")) }),
	})
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping(value = "/v2/ips-json/{manifestId}", produces = { "application/json" })
	public ResponseEntity<String> getIpsJson(
			@PathVariable("jsonId") @Parameter(name = "manifestId", description = "IPS JSON identifier", example = "manifest-id", required = true) String manifestId)
			throws GdhcnValidationException {
		String jsonContent = gdhcnService.downloadJson(manifestId);
		return ResponseEntity.of(Optional.of(jsonContent));
	}

	/**
	 * Endpoint for getting manifest data.
	 *
	 * @param manifestRequest the request body containing the manifest request data.
	 * @param manifestId      the ID of the JSON data for the manifest.
	 * @return a {@link ResponseEntity} containing the manifest data in JSON format.
	 * @throws GdhcnValidationException if there is a validation error.
	 */
	@Operation(summary = "Endpoint for getting manifest data.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Manifest retrieved successfully", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ManifestResponse.class)) }),
	})
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping(value = "/v2/manifests/{manifestId}", produces = { "application/json" })
	public ResponseEntity<ManifestResponse> getManifest(
			@RequestBody ManifestRequest manifestRequest, @PathVariable("manifestId") String manifestId)
			throws GdhcnValidationException {
		ManifestResponse response = null;
		try {
			response = gdhcnService.getManifest(manifestRequest, manifestId);
		} catch (GdhcnValidationException e) {
			throw e;
		}
		return ResponseEntity.of(Optional.of(response));
	}
}
