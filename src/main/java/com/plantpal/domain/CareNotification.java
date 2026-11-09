package com.plantpal.domain;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="care_notifications")
public class CareNotification {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private Long plantId;
 @Column(nullable=false,length=220) private String message;
 @Column(nullable=false) private Instant createdAt;
 @Column(nullable=false) private boolean read;
 protected CareNotification(){}
 public CareNotification(Long plantId,String message,Instant createdAt){
  this.plantId=plantId;this.message=message;this.createdAt=createdAt;
 }
 public Long getId(){return id;}
 public Long getPlantId(){return plantId;}
 public String getMessage(){return message;}
 public Instant getCreatedAt(){return createdAt;}
 public boolean isRead(){return read;}
 public void markRead(){read=true;}
}
