package com.plantpal.pattern;
import com.plantpal.pattern.adapter.*;
import com.plantpal.pattern.decorator.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;
class Student3PatternsTest {
 private final ObjectMapper mapper=new ObjectMapper();
 private final String mockJson="{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"{\\\"commonName\\\":\\\"Monstera\\\",\\\"scientificName\\\":\\\"Monstera deliciosa\\\",\\\"careSummary\\\":\\\"Bright indirect light\\\",\\\"lightLevel\\\":\\\"INDIRECT\\\",\\\"soilCheckDays\\\":7,\\\"identificationStatus\\\":\\\"suggested\\\"}\"}]}}]}";
 @Test void adapterConvertsGeminiEnvelopeToDomain(){
  var adapter=new GeminiPlantAdapter(request->mockJson,mapper);
  var a=adapter.recognize(new byte[]{(byte)0xff},"image/jpeg");
  assertEquals("Monstera deliciosa",a.scientificName());assertEquals(7,a.soilCheckDays());
 }
 @Test void adapterActuallySendsBase64Image(){
  final String[] request={""};
  var adapter=new GeminiPlantAdapter(json->{request[0]=json;return mockJson;},mapper);
  adapter.recognize("photo".getBytes(StandardCharsets.UTF_8),"image/png");
  assertTrue(request[0].contains("cGhvdG8="));
  assertTrue(request[0].contains("inline_data"));
 }
 @Test void adapterHandlesMissingCandidates(){
  assertThrows(ResponseStatusException.class,()->new GeminiPlantAdapter(req->"{}",mapper).recognize(new byte[]{1},"image/png"));
 }
 @Test void adapterRejectsInvalidMimeType(){
  assertThrows(IllegalArgumentException.class,()->new GeminiPlantAdapter(req->mockJson,mapper).recognize(new byte[]{1},"image/gif"));
 }
 @Test void adapterRejectsOutOfRangeRecommendation(){
  String invalid=mockJson.replace("7,", "120,");
  assertThrows(ResponseStatusException.class,()->new GeminiPlantAdapter(req->invalid,mapper).recognize(new byte[]{1},"image/png"));
 }
 @Test void decoratorAddsPlantNameWithoutChangingCore(){
  NotificationText original=new BasicNotification("Schedule updated");
  NotificationText decorated=new PlantNameDecorator(original,"Monstera");
  assertEquals("Schedule updated",original.render());
  assertEquals("Monstera: Schedule updated",decorated.render());
 }
 @Test void decoratorsCompose(){
  NotificationText msg=new CareTipDecorator(new PlantNameDecorator(new BasicNotification("Check soil"),"Pothos"),"Use your finger");
  assertEquals("Pothos: Check soil · Tip: Use your finger",msg.render());
 }
 @Test void emptyTipIsSkipped(){assertEquals("Care due",new CareTipDecorator(new BasicNotification("Care due"),"").render());}
}
