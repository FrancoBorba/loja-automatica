package br.uesb.cipec.loja_automatica.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.uesb.cipec.loja_automatica.DTO.ProductDTO;
import br.uesb.cipec.loja_automatica.controller.ControllerProduct;
import br.uesb.cipec.loja_automatica.exception.RequiredObjectIsNullException;
import br.uesb.cipec.loja_automatica.exception.ResourceNotFoundException;
import br.uesb.cipec.loja_automatica.mapper.ProductMapper;
import br.uesb.cipec.loja_automatica.model.Product;
import br.uesb.cipec.loja_automatica.repository.ProductRepository;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Service // This annotation says that the class will contain the busines rules
public class ProductService {
    
    @Autowired 
    ProductRepository repository;

    @Autowired
    ProductMapper mapper;

    // For adding loggers in he applicaiton
    // We will use the logs at the info level here
    private Logger logger = LoggerFactory.getLogger(ProductService.class.getName());

  //  Returns a ProductDTO by searching for it through its ID
  public ProductDTO findById(Long id) {
    logger.info("find one product"); 


    var entity = repository.findById(id)
                     .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    
    var dto = mapper.toDTO(entity);
    addHatoasLinks(dto);

    return dto;
  }

    // Create and returns the product
    public ProductDTO create(ProductDTO product){
      if (product == null) {
         throw new RequiredObjectIsNullException("Product cannot be null.");
      }

      logger.info("Create a product");
      var entity = mapper.toEntity(product);

      repository.save(entity);

      var dto = mapper.toDTO(entity);

      addHatoasLinks(dto);
      return dto;

    }

    // Return all Products
   public List<ProductDTO> findAll() {
    logger.info("Find all products");
    var entities = repository.findAll();
    var products = entities.stream()
                   .map(mapper::toDTO)
                   .toList();

    for (ProductDTO productDTO : products){
          addHatoasLinks(productDTO);
    }

    return products;
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
      addHatoasLinks(dto);
      return dto;
     
    }

    public void delete(Long id){

      Product entity = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Product not found"));
      logger.info("Delete  the product from Id " + id );

      // Delete the product with this id
     repository.delete(entity);
    }

    
 public  void addHatoasLinks( ProductDTO dto) {
    dto.add(linkTo(methodOn(ControllerProduct.class).findByID(dto.getId())).withSelfRel().withType("GET"));

    dto.add(linkTo(methodOn(ControllerProduct.class).findAll()).withRel("findAll").withType("GET"));

    dto.add(linkTo(methodOn(ControllerProduct.class).create(dto)).withRel("create").withType("POST"));

    dto.add(linkTo(methodOn(ControllerProduct.class).update(dto)).withRel("update").withType("PUT"));

    dto.add(linkTo(methodOn(ControllerProduct.class).delete(dto.getId())).withRel("delete").withType("DELETE"));

    
  }

    

}

