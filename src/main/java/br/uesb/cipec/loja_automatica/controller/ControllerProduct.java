package br.uesb.cipec.loja_automatica.controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.uesb.cipec.loja_automatica.DTO.ProductDTO;
import br.uesb.cipec.loja_automatica.service.ProductService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/products")
public class ControllerProduct {
    
    @Autowired
    ProductService service;
    
   
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

    @GetMapping(  
        produces = { 
      MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE ,
      MediaType.APPLICATION_YAML_VALUE}
      )
    public List<ProductDTO> findAll(){ // end point GET
    
      return service.findAll();
    }

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

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable("id")  Long id){
        service.delete(id);
    }

}
