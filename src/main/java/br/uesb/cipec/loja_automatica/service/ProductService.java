package br.uesb.cipec.loja_automatica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.uesb.cipec.loja_automatica.DTO.ProductDTO;
import br.uesb.cipec.loja_automatica.exception.RequiredObjectIsNullException;
import br.uesb.cipec.loja_automatica.exception.ResourceNotFoundException;
import br.uesb.cipec.loja_automatica.mapper.ProductMapper;
import br.uesb.cipec.loja_automatica.model.Product;
import br.uesb.cipec.loja_automatica.repository.ProductRepository;


@Service // This annotation says that the class will contain the busines rules
public class ProductService {
    
    @Autowired 
    ProductRepository repository;

    @Autowired
    ProductMapper mapper;

  //  Returns a ProductDTO by searching for it through its ID
  public ProductDTO findById(Long id) {


    var entity = repository.findById(id)
                     .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    
    var dto = mapper.toDTO(entity);

    return dto;
  }

    // Create and returns the product
    public ProductDTO create(ProductDTO product){
      if (product == null) {
         throw new RequiredObjectIsNullException("Product cannot be null.");
      }
      var entity = mapper.toEntity(product);

      repository.save(entity);

      return mapper.toDTO(entity);

    }

    // Return all Products
   public List<ProductDTO> findAll() {
    var entities = repository.findAll();
    return entities.stream()
                   .map(mapper::toDTO)
                   .toList();
}

    // Currently, to update it is necessary to pass the json of the Product with 
    // the updates
    public ProductDTO update(ProductDTO product){
      if(product == null){
       throw new RequiredObjectIsNullException("Product cannot be null.");
    }

      Product entity = repository.findById(product.getId()).
      orElseThrow(()-> new ResourceNotFoundException("Product not found")); // Return the Entity

      entity.setName(product.getName());
      entity.setAmount(product.getAmount());
      entity.setDescription(product.getDescription());
      entity.setPrice(product.getPrice());

      repository.save(entity);

      var dto = mapper.toDTO(entity);
  
      return dto;
     
    }

    public void delete(Long id){
      Product entity = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Product not found"));

      // Delete the product with this id
     repository.delete(entity);
    }

    

}

