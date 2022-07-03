package org.usth.ict.ulake.ingest.model;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.usth.ict.ulake.ingest.utils.PolicyToStringConverter;

@Entity
public class UserConfigure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    // TODO: missing JavaTypeDescriptor for custom Policy type
    @Convert(converter = PolicyToStringConverter.class)
    public Policy query;

    public Long ownerId;

    public UserConfigure() {}
}
