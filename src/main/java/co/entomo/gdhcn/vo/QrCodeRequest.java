package co.entomo.gdhcn.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * @author Uday Matta
 * @organization entomo Labs
 */
@Data
@Builder
public class QrCodeRequest {

	@Schema(description = "Passcode to unlock the QR code information", example = "passcode123")
	private String passCode;
	@Schema(description = "Expiration date of the QR code", example = "2025-04-25T14:05:15.953Z")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date expiresOn;
	@Schema(description = "FHIR resource JSON object")
	@NotBlank(message = "jsonContent is mandatory")
	private String jsonContent;
}
