package br.uesb.cipec.loja_automatica.controller.docs;

import br.uesb.cipec.loja_automatica.DTO.PurchaseResponseDTO;
import br.uesb.cipec.loja_automatica.enums.StatusPurchase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@Tag(name = "Purchase History", description = "Endpoints for viewing and managing historical purchases")
@SecurityRequirement(name = "bearerAuth")
public interface PurchaseControllerDocs {

    @Operation(summary = "Find the logged-in user's purchase history")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Purchase history found successfully",
                     content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PurchaseResponseDTO.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User must be logged in", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    ResponseEntity<Page<PurchaseResponseDTO>> findMyPurchases(
      @RequestParam(required = false) StatusPurchase status,
      @RequestParam(value = "page" , defaultValue = "0") Integer page,
      @RequestParam(value = "size" , defaultValue = "10") Integer size,
      @RequestParam(value = "direction" , defaultValue = "asc") String direction
      );

    @Operation(summary = "Find all purchases (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of all purchases found successfully",
                     content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PurchaseResponseDTO.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User must be logged in", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden - User must be an Admin", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    ResponseEntity<Page<PurchaseResponseDTO>> findAll(
      @RequestParam(value = "page" , defaultValue = "0") Integer page,
      @RequestParam(value = "size" , defaultValue = "10") Integer size,
      @RequestParam(value = "direction" , defaultValue = "asc") String direction
    );

    @Operation(summary = "Find a specific purchase by ID (Admin or Owner)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Purchase found successfully",
                     content = @Content(schema = @Schema(implementation = PurchaseResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User must be logged in", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden - User is not the owner or an Admin", content = @Content),
        @ApiResponse(responseCode = "404", description = "Purchase not found with the given ID", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    PurchaseResponseDTO findByID(@PathVariable Long id);

    @Operation(summary = "Delete a historical purchase (Admin or Owner)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Purchase deleted successfully", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User must be logged in", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden - User is not the owner or an Admin", content = @Content),
        @ApiResponse(responseCode = "404", description = "Purchase not found with the given ID", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    ResponseEntity<?> delete(@PathVariable Long id);
}