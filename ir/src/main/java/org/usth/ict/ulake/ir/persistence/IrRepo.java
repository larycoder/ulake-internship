package org.usth.ict.ulake.ir.persistence;

import javax.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.ir.model.ImgFeature;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class IrRepo implements PanacheRepository<ImgFeature> {
  
}
