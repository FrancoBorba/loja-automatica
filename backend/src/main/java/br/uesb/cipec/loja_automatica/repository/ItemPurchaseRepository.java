package br.uesb.cipec.loja_automatica.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.uesb.cipec.loja_automatica.model.ItemPurchase;


public interface ItemPurchaseRepository extends JpaRepository<ItemPurchase,Long> {
    boolean existsByProductId(Long productId);
}
