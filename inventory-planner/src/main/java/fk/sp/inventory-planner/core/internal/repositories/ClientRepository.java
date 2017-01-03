package fk.sp.inventory-planner.core.internal.repositories;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import fk.sp.inventory-planner.core.internal.entities.Client;

@Singleton
public class ClientRepository extends SimpleJpaGenericRepository<Client, String>{

  @Inject
  public ClientRepository(Provider<EntityManager> entityManagerProvider) {
    super(entityManagerProvider);
  }
}
