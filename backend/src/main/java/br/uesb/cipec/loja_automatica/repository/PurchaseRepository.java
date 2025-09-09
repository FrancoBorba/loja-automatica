package br.uesb.cipec.loja_automatica.repository;



import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.uesb.cipec.loja_automatica.enums.StatusPurchase;
import br.uesb.cipec.loja_automatica.model.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase,Long>  {
  

   // Search all purchases from a specific user
    Page<Purchase> findByUserId(Long userId , Pageable page);

  // Search all purchases from a user with a specific status
    Page<Purchase> findByUserIdAndStatus(Long userId, StatusPurchase status , Pageable pageable);

    Optional<Purchase> findByUserIdAndStatus(Long userId, StatusPurchase status);

}
