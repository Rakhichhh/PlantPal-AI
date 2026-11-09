package com.plantpal.pattern.adapter;
import com.plantpal.domain.LightLevel;
/** Validated internal representation independent of Gemini's JSON envelope. */
public record PlantAnalysis(String commonName, String scientificName, String careSummary,
                            LightLevel lightLevel, int soilCheckDays, String identificationStatus) {
 public PlantAnalysis {
  if (commonName == null || commonName.isBlank() || scientificName == null || scientificName.isBlank())
   throw new IllegalArgumentException("Plant names are required");
  if (commonName.length()>80 || scientificName.length()>120) throw new IllegalArgumentException("Plant name too long");
  if (lightLevel==null || soilCheckDays<1 || soilCheckDays>90) throw new IllegalArgumentException("Invalid care recommendation");
  if (careSummary==null)careSummary="";
  if (careSummary.length()>500) careSummary=careSummary.substring(0,500);
  if (identificationStatus==null || identificationStatus.isBlank()) identificationStatus="suggested";
 }
}
