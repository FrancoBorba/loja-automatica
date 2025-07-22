package br.uesb.cipec.loja_automatica.controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.uesb.cipec.loja_automatica.DTO.ProductDTO;
import br.uesb.cipec.loja_automatica.controller.docs.ProductsControllerDocs;
import br.uesb.cipec.loja_automatica.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/products")
@Tag(name = "Product" , description = "Endpoints for managing products")
public class ControllerProduct implements ProductsControllerDocs {
    
    @Autowired
    ProductService service;
    
    @Override
    @GetMapping(value ="/{id}" ,
        produces = {
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE,
        MediaType.APPLICATION_YAML_VALUE
        }
        )
    public ProductDTO findByID( @PathVariable("id") Long id ){
      return service.findById(id);
    }

     @Override
    @GetMapping(  
        produces = { 
      MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE ,
      MediaType.APPLICATION_YAML_VALUE}
      )
    public List<ProductDTO> findAll(){ // end point GET
    
      return service.findAll();
    }

     @Override
    @PostMapping(
        consumes =  { 
        MediaType.APPLICATION_JSON_VALUE ,
        MediaType.APPLICATION_XML_VALUE ,
        MediaType.APPLICATION_YAML_VALUE} ,
        produces = { 
        MediaType.APPLICATION_JSON_VALUE ,
        MediaType.APPLICATION_XML_VALUE ,
        MediaType.APPLICATION_YAML_VALUE}
    )
    public ProductDTO create(@RequestBody @Valid ProductDTO product){
        return service.create(product);
    }

     @Override
    @PutMapping(
        consumes =  { 
        MediaType.APPLICATION_JSON_VALUE ,
        MediaType.APPLICATION_XML_VALUE ,
        MediaType.APPLICATION_YAML_VALUE} ,
        produces = { 
        MediaType.APPLICATION_JSON_VALUE ,
        MediaType.APPLICATION_XML_VALUE ,
        MediaType.APPLICATION_YAML_VALUE}
    )
    public ProductDTO update(@RequestBody @Valid ProductDTO product){
        return service.update(product);
    }

    @Override
    @DeleteMapping(value = "/{id}")
    public ResponseEntity delete(@PathVariable("id")  Long id){
      service.delete(id);

     return ResponseEntity.noContent().build(); // return the right status code (204)
    }

}
