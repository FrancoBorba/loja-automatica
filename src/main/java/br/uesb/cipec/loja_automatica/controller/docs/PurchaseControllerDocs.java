package br.uesb.cipec.loja_automatica.controller.docs;

import java.util.List;

import org.springframework.http.MediaType;

import br.uesb.cipec.loja_automatica.DTO.PurchaseRequestDTO;
import br.uesb.cipec.loja_automatica.DTO.PurchaseResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


public interface PurchaseControllerDocs {

    @Operation(
    summary = "Find by id" ,
    description = "Finds a especification purchase by your ID",
    tags = {"Purchase"},
    responses = {
      @ApiResponse(description = "Success" , responseCode = "200" ,
       content = @Content(
        schema = @Schema(implementation = PurchaseResponseDTO.class)
       )),
      @ApiResponse(description = "No content" , responseCode = "204" , content = @Content),
      @ApiResponse(description = "Bad Request" , responseCode = "400" , content = @Content),
      @ApiResponse(description = "Unautorizhed" , responseCode = "401" , content = @Content),
      @ApiResponse(description = "Not found" , responseCode = "404" , content = @Content),
      @ApiResponse(description = "Internal Server Erros" , responseCode = "500" , content = @Content),
    }
  ) 
  public PurchaseResponseDTO findByID(Long id);

  
     @Operation(
    summary = "Adds a new Purchase" ,
    description = "Adding a Purchase ",
    tags = {"Purchase"},
    responses = {
      @ApiResponse(description = "Success" , responseCode = "200" ,
       content = @Content(
        schema = @Schema(implementation = PurchaseResponseDTO.class)
       )),
      @ApiResponse(description = "No content" , responseCode = "204" , content = @Content),
      @ApiResponse(description = "Bad Request" , responseCode = "400" , content = @Content),
      @ApiResponse(description = "Unautorizhed" , responseCode = "401" , content = @Content),
      @ApiResponse(description = "Not found" , responseCode = "404" , content = @Content),
      @ApiResponse(description = "Internal Server Erros" , responseCode = "500" , content = @Content),
    }
  ) 
  public PurchaseResponseDTO create(PurchaseRequestDTO purchaseRequestDTO);

       @Operation(
    summary = "Update a new Purchase" ,
    description = "Update a Purchase ",
    tags = {"Purchase"},
    responses = {
      @ApiResponse(description = "Success" , responseCode = "200" ,
       content = @Content(
        schema = @Schema(implementation = PurchaseResponseDTO.class)
       )),
      @ApiResponse(description = "No content" , responseCode = "204" , content = @Content),
      @ApiResponse(description = "Bad Request" , responseCode = "400" , content = @Content),
      @ApiResponse(description = "Unautorizhed" , responseCode = "401" , content = @Content),
      @ApiResponse(description = "Not found" , responseCode = "404" , content = @Content),
      @ApiResponse(description = "Internal Server Erros" , responseCode = "500" , content = @Content),
    }
  ) 
  public PurchaseResponseDTO update(Long id ,PurchaseRequestDTO purchaseRequestDTO);

     @Operation(
    summary = "Delete a purchase" ,
    description = "Delete a purchase ",
    tags = {"Purchase"},
    responses = {
      @ApiResponse(description = "Success" , responseCode = "200" ,
       content = @Content(
        schema = @Schema(implementation = PurchaseResponseDTO.class)
       )),
      @ApiResponse(description = "No content" , responseCode = "204" , content = @Content),
      @ApiResponse(description = "Bad Request" , responseCode = "400" , content = @Content),
      @ApiResponse(description = "Unautorizhed" , responseCode = "401" , content = @Content),
      @ApiResponse(description = "Not found" , responseCode = "404" , content = @Content),
      @ApiResponse(description = "Internal Server Erros" , responseCode = "500" , content = @Content),
    }
  ) 
  public void delete(Long id);

    @Operation(
    summary = "Find all Purchase",
    tags = {"Purchase"},
    responses = {
         @ApiResponse(description = "Success" , responseCode = "200" ,
     content = {
      @Content( mediaType = MediaType.APPLICATION_JSON_VALUE,
     array = @ArraySchema(schema = @Schema(implementation = PurchaseResponseDTO.class)))}),
      @ApiResponse(description = "No content" , responseCode = "204" , content = @Content),
      @ApiResponse(description = "Bad request" , responseCode = "400" , content = @Content),
      @ApiResponse(description = "Unatorizhed" , responseCode = "401" , content = @Content),
      @ApiResponse(description = "Not found" , responseCode = "404" , content = @Content),
      @ApiResponse(description = "Internal server error" , responseCode = "500", content = @Content)
    }
  )
  public List<PurchaseResponseDTO> findAll();

}
