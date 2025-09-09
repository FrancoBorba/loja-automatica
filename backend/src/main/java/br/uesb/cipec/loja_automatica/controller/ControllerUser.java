package br.uesb.cipec.loja_automatica.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.uesb.cipec.loja_automatica.DTO.UserResponseDTO;
import br.uesb.cipec.loja_automatica.DTO.UserUpdateDTO;
import br.uesb.cipec.loja_automatica.controller.docs.UserControllerDocs;
import br.uesb.cipec.loja_automatica.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User" , description = "Endpoints for managing users")
public class ControllerUser implements UserControllerDocs {
    
    @Autowired
    UserService service;

    @Override
    @GetMapping(value ="/{id}" ,
    produces = {
    MediaType.APPLICATION_JSON_VALUE,
    MediaType.APPLICATION_XML_VALUE,
    MediaType.APPLICATION_YAML_VALUE
    }
    )
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDTO findByID( @PathVariable("id") Long id ){
        return service.findByIdResponseDTO(id);
    }

     @Override
    @GetMapping(  
    produces = { 
      MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE ,
      MediaType.APPLICATION_YAML_VALUE}
      )
  @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDTO>> findAll(
     @RequestParam(value = "page" , defaultValue = "0") Integer page,
      @RequestParam(value = "size" , defaultValue = "10") Integer size,
      @RequestParam(value = "direction" , defaultValue = "asc") String direction
  ){ 

      var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;

      Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection,"name"));

      return ResponseEntity.ok(service.findAll(pageable));
    }

     @Override
    @PutMapping( value = "/me" ,
        consumes =  { 
        MediaType.APPLICATION_JSON_VALUE ,
        MediaType.APPLICATION_XML_VALUE ,
        MediaType.APPLICATION_YAML_VALUE} ,
        produces = { 
        MediaType.APPLICATION_JSON_VALUE ,
        MediaType.APPLICATION_XML_VALUE ,
        MediaType.APPLICATION_YAML_VALUE}
    )
    public UserResponseDTO update(@RequestBody @Valid UserUpdateDTO user){
        return service.update(user);
    }

    @Override
    @DeleteMapping("/me")
    public ResponseEntity<?> delete(){
        service.deleteMyAccount();
        return ResponseEntity.noContent().build(); // return the right status code (204)
    }


   
    @GetMapping("/me")
    public UserResponseDTO getMyProfile() {
        return service.getMyProfile();
    }
}
