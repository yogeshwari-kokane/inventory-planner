package fk.sp.inventory-planner.core.internal.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import fk.sp.inventory-planner.core.internal.entities.Client;
import fk.sp.inventory-planner.core.internal.repositories.ClientRepository;

@Singleton
public class ClientCache {

  private final ClientRepository clientRepository;
  private final LoadingCache<String, Optional<Client>> cache;

  @Inject
  public ClientCache(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
    this.cache = CacheBuilder.newBuilder()
        .maximumSize(100)
        .expireAfterWrite(2, TimeUnit.HOURS)
        .build(
            new CacheLoader<String, Optional<Client>>() {
              @Override
              public Optional<Client> load(String key) throws Exception {
                return clientRepository.findOne(key);
              }
            });
  }

  public Optional<Client> getClient(String name) {
    //If DB goes down this will fail
    return this.cache.getUnchecked(name);
  }

  public Set<String> populateCache() {
    this.cache.invalidateAll();
    this.cache.putAll(this.clientRepository.findAll()
                          .stream()
                          .collect(Collectors.toMap(Client::getName, Optional::of)));
    return this.cache.asMap().keySet();

  }


}
