package br.uesb.cipec.loja_automatica.repository;

import br.uesb.cipec.loja_automatica.model.Product;
import jakarta.persistence.LockModeType;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;


// This interface provides CRUD methods for Product

public interface ProductRepository extends JpaRepository<Product,Long>{
  
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithLock(Long id);
}