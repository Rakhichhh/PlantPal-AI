package com.plantpal.pattern.adapter;
/** Internal target interface; implementations may consume foreign AI schemas. */
public interface PlantRecognitionProvider {
 PlantAnalysis recognize(byte[] image, String mimeType);
}
