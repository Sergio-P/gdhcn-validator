package co.entomo.gdhcn.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * @author Uday Matta
 * @organization entomo Labs
 */
@Data
@Builder
public class ManifestRequest {
    @Schema(description = "Name of the subject that is going to unlock the data", example = "John Doe")
    private String recipient;
    @Schema(description = "Passcode to unlock the QR code information", example = "passcode123")
    private String passcode;
}
