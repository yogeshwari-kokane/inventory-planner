package fk.sp.inventory-planner.core.internal.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Client {

  @Id
  private String name;
  private String email;
  private String description;
}
