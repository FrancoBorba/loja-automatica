package br.uesb.cipec.loja_automatica.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import br.uesb.cipec.loja_automatica.DTO.ProductDTO;

import br.uesb.cipec.loja_automatica.controller.ControllerProduct;
import br.uesb.cipec.loja_automatica.exception.InvalidProductDataException;

import br.uesb.cipec.loja_automatica.exception.RequiredObjectIsNullException;
import br.uesb.cipec.loja_automatica.exception.ResourceNotFoundException;
import br.uesb.cipec.loja_automatica.mapper.ProductMapper;
import br.uesb.cipec.loja_automatica.model.Product;
import br.uesb.cipec.loja_automatica.repository.ItemPurchaseRepository;
import br.uesb.cipec.loja_automatica.repository.ProductRepository;



@Service // This annotation says that the class will contain the busines rules
public class ProductService {
    
    @Autowired 
    ProductRepository repository;

    @Autowired
    ProductMapper mapper;

    @Autowired
    ItemPurchaseRepository itemPurchaseRepository; 

    // For adding loggers in he applicaiton
    // We will use the logs at the info level here
    private Logger logger = LoggerFactory.getLogger(ProductService.class.getName());

  //  Returns a ProductDTO by searching for it through its ID
  public ProductDTO findById(Long id) {
    logger.info("find one product"); 


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

      if (product.getName() == null || product.getName().isBlank()) {
        throw new InvalidProductDataException("Product name cannot be null or empty.");
      }
      if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
          throw new InvalidProductDataException("Product price cannot be negative.");
      }
      if (product.getAmount() == null || product.getAmount() < 0) {
          throw new InvalidProductDataException("Product stock amount cannot be negative.");
      }
      
      logger.info("Create a product");
      var entity = mapper.toEntity(product);

      repository.save(entity);

      var dto = mapper.toDTO(entity);

      return dto;

    }

    // Return all Products
   public Page<ProductDTO> findAll(Pageable pageable) {
    
    logger.info("Find all products");

    Page<Product> productPage = repository.findAll(pageable);

  
    return productPage.map(mapper::toDTO);
}

    // Currently, to update it is necessary to pass the json of the Product with 
    // the updates
    public ProductDTO update(ProductDTO product){
      if(product == null){
       throw new RequiredObjectIsNullException("Product cannot be null.");
    }

     logger.info("Update the product " + product.getName());
    
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
      logger.info("Delete  the product from Id " + id );
      if (itemPurchaseRepository.existsByProductId(id)) {
        throw new ProductInUseException("Cannot delete a product that is part of one or more existing purchases.");
      }
      // Delete the product with this id
     repository.delete(entity);
    }

    


    

}

