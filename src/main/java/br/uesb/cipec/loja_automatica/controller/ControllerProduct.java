package br.uesb.cipec.loja_automatica.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.uesb.cipec.loja_automatica.model.Product;
import br.uesb.cipec.loja_automatica.service.ProductServices;


@RestController
@RequestMapping("/products")
public class ControllerProduct {
    
    @Autowired
    ProductServices service;
    
   
    @GetMapping(value ="/{id}" ,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    
    public Product findByID( @PathVariable("id") Long id ){
      return service.findById(id);
    }

    /*@GetMapping(  
        produces = { MediaType.APPLICATION_JSON_VALUE})
    public List<Product> findAll(){ // end point GET
    
      return service.findAll();
    }*/

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

    public Product create(@RequestBody Product product){
        return service.create(product);
    }
    
    

}
