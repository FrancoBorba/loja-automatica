package br.uesb.cipec.loja_automatica.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import br.uesb.cipec.loja_automatica.enums.StatusPurchase;
import br.uesb.cipec.loja_automatica.model.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase,Long>  {
  

   // Search all purchases from a specific user
    List<Purchase> findByUserId(Long userId);

  // Search all purchases from a user with a specific status
    List<Purchase> findByUserIdAndStatus(Long userId, StatusPurchase status);
}
