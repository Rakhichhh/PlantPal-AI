package com.plantpal.api;
import com.plantpal.service.PlantPhotoService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RestController @RequestMapping("/api")
public class PlantRecognitionController {
 private final PlantPhotoService service;
 public PlantRecognitionController(PlantPhotoService service){this.service=service;}
 @PostMapping(value="/plants/analyze",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
 public PlantPhotoService.PlantPhotoResult analyze(@RequestParam("photo") MultipartFile photo){return service.analyze(photo);}
 @GetMapping("/plant-photos/{filename}")
 public ResponseEntity<byte[]> photo(@PathVariable String filename){
  var photo=service.read(filename);
  return ResponseEntity.ok().contentType(photo.contentType()).cacheControl(CacheControl.noCache()).body(photo.content());
 }
}
