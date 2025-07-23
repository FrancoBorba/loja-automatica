package br.uesb.cipec.loja_automatica.controller.docs;

import java.util.List;



import br.uesb.cipec.loja_automatica.DTO.ProductDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/*
It's a documentation interface for Swagger/OpenAPI in your Spring Boot project, used to:
Organize, standardize, and automatically generate REST documentation for Product-related endpoints.

The ProductsControllerDocs interface contains only the endpoint documentation, leaving the code clean and allowing the updating and viewing of REST contracts in isolation.

----------------------------------------------------------------------------------
@Operation
Defines the description of each REST endpoint:

summary: Short text displayed in Swagger UI.

description: More detailed explanation.

tags: Group in Swagger UI, useful for categorizing endpoints in this case: (Product)
----------------------------------------------------------------------------------
@ApiResponse
Defines the possible HTTP responses that the endpoint can return:

description: Explanation of the code.

responseCode: HTTP status returned (200, 204, 400, 404, 500).

content: Defines the expected response type (JSON, XML).
----------------------------------------------------------------------------------
@Content, @Schema, and @ArraySchema
Define how Swagger will display the response data:

@Schema(implementation = ProductDTO.class): Indicates that the return structure is the same as the ProductDTO DTO.

@ArraySchema(...): Indicates that the return is a list of ProductDTOs.

mediaType = MediaType.APPLICATION_JSON_VALUE: Indicates that the response is in JSON format.

 */
public interface ProductsControllerDocs {

  @Operation(
    summary = "Find all products",
    tags = {"Product"},
    responses = {
         @ApiResponse(description = "Success" , responseCode = "200" ,
     content = {
      @Content( mediaType = MediaType.APPLICATION_JSON_VALUE,
     array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class)))}),
      @ApiResponse(description = "No content" , responseCode = "204" , content = @Content),
      @ApiResponse(description = "Bad request" , responseCode = "400" , content = @Content),
      @ApiResponse(description = "Unatorizhed" , responseCode = "401" , content = @Content),
      @ApiResponse(description = "Not found" , responseCode = "404" , content = @Content),
      @ApiResponse(description = "Internal server error" , responseCode = "500", content = @Content)
    }
  )
  public List<ProductDTO> findAll();

  @Operation(
    summary = "Find by id" ,
    description = "Finds a especification product by your ID",
    tags = {"Product"},
    responses = {
      @ApiResponse(description = "Success" , responseCode = "200" ,
       content = @Content(
        schema = @Schema(implementation = ProductDTO.class)
       )),
      @ApiResponse(description = "No content" , responseCode = "204" , content = @Content),
      @ApiResponse(description = "Bad Request" , responseCode = "400" , content = @Content),
      @ApiResponse(description = "Unautorizhed" , responseCode = "401" , content = @Content),
      @ApiResponse(description = "Not found" , responseCode = "404" , content = @Content),
      @ApiResponse(description = "Internal Server Erros" , responseCode = "500" , content = @Content),
    }
  ) 
  ProductDTO findByID( // end point GET
      Long id);

   @Operation(
    summary = "Adds a new Product" ,
    description = "Adding a Product ",
    tags = {"Product"},
    responses = {
      @ApiResponse(description = "Success" , responseCode = "200" ,
       content = @Content(
        schema = @Schema(implementation = ProductDTO.class)
       )),
      @ApiResponse(description = "No content" , responseCode = "204" , content = @Content),
      @ApiResponse(description = "Bad Request" , responseCode = "400" , content = @Content),
      @ApiResponse(description = "Unautorizhed" , responseCode = "401" , content = @Content),
      @ApiResponse(description = "Not found" , responseCode = "404" , content = @Content),
      @ApiResponse(description = "Internal Server Erros" , responseCode = "500" , content = @Content),
    }
  ) 
  ProductDTO create(ProductDTO product);

     @Operation(
    summary = "Update a product information" ,
    description = "Update a product information ",
    tags = {"Product"},
    responses = {
      @ApiResponse(description = "Success" , responseCode = "200" ,
       content = @Content(
        schema = @Schema(implementation = ProductDTO.class)
       )),
      @ApiResponse(description = "No content" , responseCode = "204" , content = @Content),
      @ApiResponse(description = "Bad Request" , responseCode = "400" , content = @Content),
      @ApiResponse(description = "Unautorizhed" , responseCode = "401" , content = @Content),
      @ApiResponse(description = "Not found" , responseCode = "404" , content = @Content),
      @ApiResponse(description = "Internal Server Erros" , responseCode = "500" , content = @Content),
    }
  ) 
  ProductDTO update(ProductDTO product);

   @Operation(
    summary = "Delete a product" ,
    description = "Delete a product ",
    tags = {"Product"},
    responses = {
      @ApiResponse(description = "Success" , responseCode = "200" ,
       content = @Content(
        schema = @Schema(implementation = ProductDTO.class)
       )),
      @ApiResponse(description = "No content" , responseCode = "204" , content = @Content),
      @ApiResponse(description = "Bad Request" , responseCode = "400" , content = @Content),
      @ApiResponse(description = "Unautorizhed" , responseCode = "401" , content = @Content),
      @ApiResponse(description = "Not found" , responseCode = "404" , content = @Content),
      @ApiResponse(description = "Internal Server Erros" , responseCode = "500" , content = @Content),
    }
  ) 
  ResponseEntity<?> delete(Long id);
} 
