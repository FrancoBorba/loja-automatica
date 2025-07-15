package br.uesb.cipec.loja_automatica.repository;

import br.uesb.cipec.loja_automatica.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ProductRepository extends JpaRepository<Product,Long>{
    
}