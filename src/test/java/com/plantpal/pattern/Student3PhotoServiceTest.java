package com.plantpal.pattern;
import com.plantpal.pattern.adapter.*;
import com.plantpal.domain.LightLevel;
import com.plantpal.service.PlantPhotoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import java.nio.file.Path;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;
class Student3PhotoServiceTest {
 @TempDir Path folder;
 private PlantPhotoService service(){
  PlantRecognitionProvider provider=(image,mime)->new PlantAnalysis("Monstera","Monstera deliciosa","Check soil",LightLevel.INDIRECT,7,"suggested");
  return new PlantPhotoService(provider,folder.toString());
 }
 @Test void uploadedPngIsSavedAndCanBeRead(){
  var img=new MockMultipartFile("photo","leaf.png","image/png",new byte[]{(byte)137,80,78,71,13,10,26,10,1,2});
  var s=service();var out=s.analyze(img);
  assertEquals("Monstera",out.analysis().commonName());
  var content=s.read(out.photoUrl().replace("/api/plant-photos/",""));
  assertEquals(10,content.content().length);
 }
 @Test void rejectsInvalidFileSignature(){
  var img=new MockMultipartFile("photo","fake.png","image/png",new byte[]{1,2,3});
  assertThrows(IllegalArgumentException.class,()->service().analyze(img));
 }
 @Test void rejectsOversizedFile(){
  var tooLarge=new byte[5*1024*1024+1];Arrays.fill(tooLarge,(byte)1);
  var img=new MockMultipartFile("photo","big.jpg","image/jpeg",tooLarge);
  assertThrows(IllegalArgumentException.class,()->service().analyze(img));
 }
 @Test void refusesArbitraryFilePath(){
  assertThrows(org.springframework.web.server.ResponseStatusException.class,()->service().read("../../secret"));
 }
}
