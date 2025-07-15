package br.uesb.cipec.loja_automatica.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.uesb.cipec.loja_automatica.model.Product;
import br.uesb.cipec.loja_automatica.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;

@Service // This annotation says that the class will contain the busines rules
public class ProductServices {
    
    @Autowired 
    ProductRepository repository;

  public Product findById(Long id) {
    return repository.findById(id)
                     .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
  }


    public Product create(Product product){
        return repository.save(product);
    }
    

}

