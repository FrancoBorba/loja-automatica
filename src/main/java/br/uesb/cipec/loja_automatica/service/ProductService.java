package br.uesb.cipec.loja_automatica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.uesb.cipec.loja_automatica.model.Product;
import br.uesb.cipec.loja_automatica.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;

@Service // This annotation says that the class will contain the busines rules
public class ProductService {
    
    @Autowired 
    ProductRepository repository;

  //  Returns a ProductDTO by searching for it through its ID
  public Product findById(Long id) {
    return repository.findById(id)
                     .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
  }

    // Create and returns the product
    public Product create(Product product){
      if (product == null) {
         throw new IllegalArgumentException("Product cannot be null");
      }
        return repository.save(product);
    }

    // Return all Products
    public List<Product> findAll(){

      List<Product> products;

      products = repository.findAll();

      return products;

    }

    // Currently, to update it is necessary to pass the json of the Product with 
    // the updates
    public Product update(Product product){
      if(product == null){
        throw new IllegalArgumentException("Product cannot be null");
    }

      Product productUpdate = repository.findById(product.getId()).
      orElseThrow(()-> new EntityNotFoundException("Product not found")); // Return the Entity

      productUpdate.setName(product.getName());
      productUpdate.setAmount(product.getAmount());
      productUpdate.setPrice(product.getPrice());
      productUpdate.setDescription(product.getDescription());

      return repository.save(productUpdate);
    }

    public void delete(Long id){
      Product product = repository.findById(id).orElseThrow(()-> new EntityNotFoundException("Product not found"));

      // Delete the product with this id
     repository.delete(product);
    }

    

}

