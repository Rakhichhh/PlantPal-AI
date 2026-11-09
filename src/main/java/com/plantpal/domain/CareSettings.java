package com.plantpal.domain;
import jakarta.persistence.*;
@Entity @Table(name="care_settings")
public class CareSettings {
 @Id private Long plantId;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private CareMode mode;
 protected CareSettings() { }
 public CareSettings(Long plantId, CareMode mode) {
  if (plantId == null || mode == null) throw new IllegalArgumentException("Plant and mode are required");
  this.plantId=plantId; this.mode=mode;
 }
 public Long getPlantId(){return plantId;}
 public CareMode getMode(){return mode;}
 public void changeMode(CareMode mode){if(mode==null)throw new IllegalArgumentException("Mode required"); this.mode=mode;}
}
