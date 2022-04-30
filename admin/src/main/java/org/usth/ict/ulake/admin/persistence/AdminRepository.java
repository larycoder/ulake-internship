package org.usth.ict.ulake.admin.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.admin.model.AdminModel;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class AdminRepository implements PanacheRepository<AdminModel> {
}
