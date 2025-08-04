package br.uesb.cipec.loja_automatica.controller;

import br.uesb.cipec.loja_automatica.DTO.*;
import br.uesb.cipec.loja_automatica.service.ItemPurchaseService;
import br.uesb.cipec.loja_automatica.service.PurchaseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/cart") // TODOS OS ENDPOINTS DO CARRINHO COMEÇARÃO COM /api/cart
@Tag(name = "Cart", description = "Endpoints for managing the active shopping cart")
public class CartController {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private ItemPurchaseService itemPurchaseService;

    @GetMapping
    public ResponseEntity<PurchaseResponseDTO> getActiveCart() {
        return purchaseService.findActiveCartByUser()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/items")
    public PurchaseResponseDTO addItemToCart(@RequestBody @Valid ItemPurchaseRequestDTO itemRequest) {
        return itemPurchaseService.addItemToCart(itemRequest);
    }

    @PatchMapping("/items/{itemId}")
    public PurchaseResponseDTO updateItemQuantity(@PathVariable Long itemId, @RequestBody @Valid UpdateItemQuantityDTO quantityDTO) {
        return itemPurchaseService.updateItemQuantity(itemId, quantityDTO.getNewQuantity());
    }

    @DeleteMapping("/items/{itemId}")
    public PurchaseResponseDTO removeItemFromCart(@PathVariable Long itemId) {
        return itemPurchaseService.removeItemFromCart(itemId);
    }

    @PostMapping("/checkout")
    public PurchaseResponseDTO checkout() {
        // O ID da compra não é mais necessário na URL, pois o serviço buscará o carrinho ativo do usuário
        return purchaseService.checkout();
    }
}