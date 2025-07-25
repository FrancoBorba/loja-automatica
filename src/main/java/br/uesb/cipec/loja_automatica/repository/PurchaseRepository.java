package br.uesb.cipec.loja_automatica.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.uesb.cipec.loja_automatica.model.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase,Long>  {
  
}
