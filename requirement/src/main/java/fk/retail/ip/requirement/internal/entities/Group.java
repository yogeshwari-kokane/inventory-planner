package fk.retail.ip.requirement.internal.entities;

import javax.persistence.Column;

/**
 * Created by nidhigupta.m on 27/01/17.
 */
public class Group extends AbstractEntity {

    private String name;

    @Column(name = "proc_type")
    private String procType;

    @Column(name = "is_enabled")
    private boolean isEnabled;

}
