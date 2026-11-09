package com.plantpal.pattern.adapter;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.plantpal.domain.LightLevel;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Base64;
/** Adapter: converts the external Gemini response/envelope into PlantAnalysis. */
@Component
public class GeminiPlantAdapter implements PlantRecognitionProvider {
 private final GeminiGateway gateway;
 private final ObjectMapper mapper;
 public GeminiPlantAdapter(GeminiGateway gateway,ObjectMapper mapper){this.gateway=gateway;this.mapper=mapper;}
 @Override public PlantAnalysis recognize(byte[] image,String mime){
  if(image==null || image.length==0)throw new IllegalArgumentException("Image cannot be empty");
  if(!"image/jpeg".equals(mime) && !"image/png".equals(mime))
   throw new IllegalArgumentException("Upload a JPG or PNG image");
  try {
   ObjectNode request=mapper.createObjectNode();
   var parts=mapper.createArrayNode();
   parts.addObject().put("text","Identify the likely houseplant in this photo. If uncertain use best guess and say uncertain. Return JSON with fields commonName (string), scientificName (string), careSummary (short string), lightLevel (LOW, INDIRECT, BRIGHT), soilCheckDays (integer 1-90), identificationStatus (suggested or uncertain). Soil check is NOT an automatic watering order. Do not claim to diagnose plant disease. If the photo is not a plant return commonName Unknown plant, scientificName Unknown, identificationStatus uncertain.");
   parts.addObject().putObject("inline_data").put("mime_type",mime)
        .put("data",Base64.getEncoder().encodeToString(image));
   request.putArray("contents").addObject().set("parts",parts);
   request.putObject("generationConfig").put("responseMimeType","application/json")
        .put("temperature",0.2);
   return adapt(gateway.generate(mapper.writeValueAsString(request)));
  }catch(ResponseStatusException | IllegalArgumentException ex){throw ex;}
   catch(Exception ex){throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Could not read AI analysis");}
 }
 /** Parses Gemini wrapper and maps its nested text JSON to our stable domain model. */
 public PlantAnalysis adapt(String response){
  try {
   JsonNode wrapper=mapper.readTree(response);
   JsonNode candidates=wrapper.path("candidates");
   if(!candidates.isArray() || candidates.isEmpty())throw new IllegalArgumentException("Gemini returned no candidates");
   JsonNode parts=candidates.get(0).path("content").path("parts");
   if(!parts.isArray() || parts.isEmpty())throw new IllegalArgumentException("Missing answer parts");
   String raw=parts.get(0).path("text").asText("").trim();
   if(raw.startsWith("```"))raw=raw.replaceFirst("(?s)^```(?:json)?\\s*","").replaceFirst("(?s)\\s*```$","");
   JsonNode data=mapper.readTree(raw);
   String light=data.path("lightLevel").asText("INDIRECT");
   LightLevel level=LightLevel.valueOf(light.toUpperCase());
   int days=data.path("soilCheckDays").asInt(7);
   return new PlantAnalysis(data.path("commonName").asText(""),data.path("scientificName").asText(""),
    data.path("careSummary").asText(""),level,days,data.path("identificationStatus").asText("suggested"));
  }catch(Exception ex){throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
     "Gemini response was incomplete. Retry or add plant manually.");}
 }
}
