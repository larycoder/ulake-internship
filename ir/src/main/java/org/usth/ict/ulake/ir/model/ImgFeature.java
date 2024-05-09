package org.usth.ict.ulake.ir.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
public class ImgFeature {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public Long id;

  @Schema(description = "Extracted Image File Id")
  public Long fid;

  @Schema(description = "Image Owner Id")
  public Long uid;

  @Lob
  @Schema(description = "Image extracted feature value using Hist")
  public String featureValueHist;

  @Lob
  @Schema(description = "Image extracted feature value using Hist - fallback column")
  public String featureValue;

  @Lob
  @Schema(description = "Image extracted feature value using GLCM")
  public String featureValueGLCM;


}
