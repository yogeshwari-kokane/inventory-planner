package fk.sp.inventory-planner.core.internal.services;

import com.google.inject.Inject;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import fk.sp.common.extensions.jpa.Page;
import fk.sp.common.extensions.jpa.PageRequest;
import fk.sp.inventory-planner.core.internal.entities.Client;
import fk.sp.inventory-planner.core.internal.repositories.ClientRepository;
import fk.sp.inventory-planner.core.models.ClientsResponse;

@Singleton
public class ClientService {

  private final Object lock = new Object();
  private final ClientRepository clientRepository;
  private Set<String> clientIds;

  @Inject
  public ClientService(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
    this.clientIds = new HashSet<>();
  }

  public List<Client> getClients() {
    return this.clientRepository.findAll();
  }

  public void reloadClientIds() {
    synchronized (this.lock) {
      Set<String>
          clientIds =
          this.clientRepository.findAll().stream().map(Client::getName).collect(Collectors.toSet());
      this.clientIds = clientIds;
    }
  }

  public ClientsResponse getClientsPaginated(int pageNo, int pageSize) {
    PageRequest pageRequest = PageRequest
        .builder()
        .pageNumber(pageNo - 1)
        .pageSize(pageSize)
        .build();
    Page<Client> clientPage = clientRepository.findAll(pageRequest);
    ClientsResponse clientsResponse = new ClientsResponse();
    clientsResponse.setTotal(clientPage.getTotalCount());
    clientsResponse.setClients(clientPage.getContent());
    return clientsResponse;
  }

  public Client createClient(Client client) {
    clientRepository.persist(client);
    return client;
  }

  public Optional<Client> getClient(String clientName) {
    return clientRepository.findOne(clientName);
  }
}
