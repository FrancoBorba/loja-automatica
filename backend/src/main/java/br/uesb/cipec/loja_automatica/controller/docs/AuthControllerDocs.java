package br.uesb.cipec.loja_automatica.controller.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import br.uesb.cipec.loja_automatica.DTO.UserLoginDTO;
import br.uesb.cipec.loja_automatica.DTO.UserRegisterDTO;
import br.uesb.cipec.loja_automatica.DTO.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;



public interface AuthControllerDocs {

    @Operation(
        summary = "Register a new user",
        tags = {"Authentication"} ,
        description = "Creates a new user account and returns the created user's data without the password.",
        responses = {
            @ApiResponse(responseCode = "200", description = "User registered successfully", 
                         content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or email already in use", 
                         content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", 
                         content = @Content)
        }
    )
    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterDTO userDTO);


    @Operation(
        summary = "Authenticate a user",
        tags = {"Authentication"} ,
        description = "Validates user credentials and returns a JWT token upon success.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Authentication successful", 
                         content = @Content(schema = @Schema(example = "{\"token\": \"your.jwt.token.here\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", 
                         content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials", 
                         content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", 
                         content = @Content)
        }
    )
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginDTO loginRequest);

}