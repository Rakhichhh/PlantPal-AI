package com.plantpal.service;
import com.plantpal.pattern.adapter.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.io.IOException;
import java.util.UUID;
/** Validates uploaded pictures, then delegates AI format adaptation to Adapter. */
@Service
public class PlantPhotoService {
 private final PlantRecognitionProvider recognition;
 private final Path uploadDir;
 public PlantPhotoService(PlantRecognitionProvider recognition,@Value("${plantpal.photos.directory:./data/photos}") String dir){
  this.recognition=recognition;this.uploadDir=Paths.get(dir).toAbsolutePath().normalize();
 }
 public PlantPhotoResult analyze(MultipartFile file){
  try {
   if(file==null || file.isEmpty())throw new IllegalArgumentException("Choose an image first");
   if(file.getSize()>5*1024*1024)throw new IllegalArgumentException("Maximum image size is 5 MB");
   byte[] bytes=file.getBytes();
   String mime=detectType(bytes);
   // AI must succeed before saving, so failed analyses leave no unused image files.
   PlantAnalysis result=recognition.recognize(bytes,mime);
   Files.createDirectories(uploadDir);
   String filename=UUID.randomUUID()+("image/png".equals(mime)?".png":".jpg");
   Files.write(uploadDir.resolve(filename),bytes,StandardOpenOption.CREATE_NEW);
   return new PlantPhotoResult(result,"/api/plant-photos/"+filename);
  }catch(IOException ex){throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Could not save image");}
 }
 public PhotoFile read(String name){
  if(!name.matches("[0-9a-fA-F-]{36}\\.(jpg|png)"))throw new ResponseStatusException(HttpStatus.NOT_FOUND);
  Path path=uploadDir.resolve(name).normalize();
  if(!path.startsWith(uploadDir)||!Files.isRegularFile(path))throw new ResponseStatusException(HttpStatus.NOT_FOUND);
  try{return new PhotoFile(Files.readAllBytes(path),name.endsWith(".png")?MediaType.IMAGE_PNG:MediaType.IMAGE_JPEG);}
  catch(IOException ex){throw new ResponseStatusException(HttpStatus.NOT_FOUND);}
 }
 static String detectType(byte[] data){
  if(data.length>=3 && (data[0]&255)==0xff && (data[1]&255)==0xd8 && (data[2]&255)==0xff)return "image/jpeg";
  if(data.length>=8 && (data[0]&255)==137 && data[1]==80 && data[2]==78 && data[3]==71 && data[4]==13 && data[5]==10 && (data[6]&255)==26 && data[7]==10)return "image/png";
  throw new IllegalArgumentException("Only valid JPG and PNG files are supported");
 }
 public record PlantPhotoResult(PlantAnalysis analysis,String photoUrl){}
 public record PhotoFile(byte[] content,MediaType contentType){}
}
