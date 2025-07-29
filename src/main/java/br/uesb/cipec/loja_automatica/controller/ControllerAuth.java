package br.uesb.cipec.loja_automatica.controller;




import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.uesb.cipec.loja_automatica.DTO.UserLoginDTO;
import br.uesb.cipec.loja_automatica.DTO.UserRegisterDTO;
import br.uesb.cipec.loja_automatica.DTO.UserResponseDTO;
import br.uesb.cipec.loja_automatica.controller.docs.AuthControllerDocs;
import br.uesb.cipec.loja_automatica.exception.ResourceNotFoundException;
import br.uesb.cipec.loja_automatica.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/auth")
@Tag(name = "Authentication", description = "Endpoints for User Registration and Login")
public class ControllerAuth implements AuthControllerDocs {
    
 
    
    @Autowired
    UserService service;

    @Override
    @PostMapping( value= "/register",
        consumes =  { 
        MediaType.APPLICATION_JSON_VALUE ,
        MediaType.APPLICATION_XML_VALUE ,
        MediaType.APPLICATION_YAML_VALUE} ,
        produces = { 
        MediaType.APPLICATION_JSON_VALUE ,
        MediaType.APPLICATION_XML_VALUE ,
        MediaType.APPLICATION_YAML_VALUE}
    )
    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterDTO user){
        try { 
        UserResponseDTO userResponse = service.create(user);
        return ResponseEntity.ok(userResponse);
    } catch (IllegalArgumentException e) {
        // Captura o erro do service e retorna uma resposta 400 Bad Request
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    }

    // this method validates the authentication, then returns a JWT token 
    @Override
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginDTO loginRequest){
     try {
        String token = service.authenticate(loginRequest);
        return ResponseEntity.ok(Map.of("token", token));
    } catch (ResourceNotFoundException e) {
        return ResponseEntity.status(401).body(e.getMessage());
    }
    }

 


}
