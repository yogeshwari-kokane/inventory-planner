package fk.sp.inventory-planner.core.models;

import java.util.List;

import fk.sp.inventory-planner.core.internal.entities.Client;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.Data;

@Data
@JsonSnakeCase
public class ClientsResponse {

  private long total;
  private List<Client> clients;
}
