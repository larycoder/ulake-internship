package org.usth.ict.ulake.ir.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
public class ImgFeature {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public Long id;

  @Schema(description = "Extracted Image File Id")
  public Long fid;

  @Lob 
  @Schema(description = "Image extracted feature value")
  public String featureValue;

}
