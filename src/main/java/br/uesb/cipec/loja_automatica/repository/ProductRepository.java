package br.uesb.cipec.loja_automatica.repository;

import br.uesb.cipec.loja_automatica.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;


// This interface provides CRUD methods for Product

public interface ProductRepository extends JpaRepository<Product,Long>{
    
}