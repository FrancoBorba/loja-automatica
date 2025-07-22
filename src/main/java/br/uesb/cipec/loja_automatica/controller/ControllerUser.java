package br.uesb.cipec.loja_automatica.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.uesb.cipec.loja_automatica.model.User;
import br.uesb.cipec.loja_automatica.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class ControllerUser {
    
    @Autowired
    UserService service;

    @GetMapping(value ="/{id}" ,
    produces = {
    MediaType.APPLICATION_JSON_VALUE,
    MediaType.APPLICATION_XML_VALUE,
    MediaType.APPLICATION_YAML_VALUE
    }
    )
    public ResponseEntity<User> findByID( @PathVariable("id") Long id ){
        return service.findById(id).
               map(ResponseEntity::ok).
               orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(  
        produces = { 
      MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE ,
      MediaType.APPLICATION_YAML_VALUE}
      )
    public List<User> findAll(){ 
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
    public User create(@RequestBody @Valid User user){
        return service.create(user);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable("id")  Long id){
        service.delete(id);
    }
}
