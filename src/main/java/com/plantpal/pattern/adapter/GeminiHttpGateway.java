package com.plantpal.pattern.adapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
/** Network gateway. Never exposes API key to browser or source files. */
@Component
public class GeminiHttpGateway implements GeminiGateway {
 private final String apiKey;
 private final String model;
 private final HttpClient client=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
 public GeminiHttpGateway(@Value("${plantpal.gemini.api-key:}") String apiKey,
                          @Value("${plantpal.gemini.model:gemini-2.5-flash}") String model){
  this.apiKey=apiKey;this.model=model;
 }
 @Override public String generate(String requestJson){
  if(apiKey.isBlank()) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
   "Gemini key not configured. Set GEMINI_API_KEY, or add the plant manually.");
  if(!model.matches("[a-zA-Z0-9._-]+"))throw new IllegalStateException("Invalid Gemini model setting");
  try {
   var req=HttpRequest.newBuilder(URI.create("https://generativelanguage.googleapis.com/v1beta/models/"+model+":generateContent"))
    .timeout(Duration.ofSeconds(35)).header("Content-Type","application/json")
    .header("x-goog-api-key",apiKey).POST(HttpRequest.BodyPublishers.ofString(requestJson,StandardCharsets.UTF_8)).build();
   var result=client.send(req,HttpResponse.BodyHandlers.ofString());
   if(result.statusCode()!=200) throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
     "Gemini API unavailable (HTTP "+result.statusCode()+"). Try again or enter plant details manually.");
   return result.body();
  }catch(InterruptedException ex){Thread.currentThread().interrupt();throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"AI request interrupted");}
   catch(java.io.IOException ex){throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"AI connection unavailable");}
 }
}
