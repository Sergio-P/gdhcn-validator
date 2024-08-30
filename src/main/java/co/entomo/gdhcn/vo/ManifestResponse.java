package co.entomo.gdhcn.vo;

import java.util.Map;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public class ManifestResponse {
  @Schema(name = "files", example = "[{\"contentType\":\"application/fhir+json\",\"location\":\"http://example-url.com/v2/ips-json/id?key=key-string\"}]")
  public List<Map<String, String>> files;
}
