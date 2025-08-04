package br.uesb.cipec.loja_automatica.controller.docs;

import br.uesb.cipec.loja_automatica.DTO.ItemPurchaseRequestDTO;
import br.uesb.cipec.loja_automatica.DTO.PurchaseResponseDTO;
import br.uesb.cipec.loja_automatica.DTO.UpdateItemQuantityDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

// Todos os endpoints aqui são para usuários autenticados
@Tag(name = "Cart", description = "Endpoints for managing the active shopping cart")
@SecurityRequirement(name = "bearerAuth") // Aplica o cadeado de segurança em todos os endpoints desta interface
public interface CartControllerDocs {

    @Operation(summary = "Get user's active shopping cart",
        responses = {
            @ApiResponse(responseCode = "200", description = "Active cart found", content = @Content(schema = @Schema(implementation = PurchaseResponseDTO.class))),
            @ApiResponse(responseCode = "204", description = "No active cart found for the user", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
        })
    ResponseEntity<PurchaseResponseDTO> getActiveCart();

    @Operation(summary = "Add an item to the cart", description = "Adds a new product to the active cart. If the product already exists, its quantity is incremented. If no active cart exists, one is created automatically.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Item added or quantity incremented successfully", content = @Content(schema = @Schema(implementation = PurchaseResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request (e.g., invalid product ID or quantity)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
        })
    PurchaseResponseDTO addItemToCart(@RequestBody @Valid ItemPurchaseRequestDTO itemRequest);

    @Operation(summary = "Update an item's quantity in the cart", description = "Sets a new quantity for a specific item in the active cart. If the new quantity is 0, the item is removed.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Item quantity updated successfully", content = @Content(schema = @Schema(implementation = PurchaseResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request (e.g., negative quantity)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Item not found in cart", content = @Content)
        })
    PurchaseResponseDTO updateItemQuantity(@PathVariable Long itemId, @RequestBody @Valid UpdateItemQuantityDTO quantityDTO);

    @Operation(summary = "Remove an item from the cart",
        responses = {
            @ApiResponse(responseCode = "200", description = "Item removed successfully", content = @Content(schema = @Schema(implementation = PurchaseResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Item not found in cart", content = @Content)
        })
    PurchaseResponseDTO removeItemFromCart(@PathVariable Long itemId);

    @Operation(summary = "Checkout the active cart", description = "Finalizes the active cart, changing its status and turning it into a historical purchase order.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Checkout successful", content = @Content(schema = @Schema(implementation = PurchaseResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Active cart not found or is empty", content = @Content)
        })
    PurchaseResponseDTO checkout();
}