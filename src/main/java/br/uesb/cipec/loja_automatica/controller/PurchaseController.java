package br.uesb.cipec.loja_automatica.controller;

import br.uesb.cipec.loja_automatica.DTO.PurchaseResponseDTO;
import br.uesb.cipec.loja_automatica.controller.docs.PurchaseControllerDocs;
import br.uesb.cipec.loja_automatica.enums.StatusPurchase;
import br.uesb.cipec.loja_automatica.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases") // MUDAMOS PARA O PLURAL
public class PurchaseController implements PurchaseControllerDocs {

    @Autowired
    private PurchaseService purchaseService;

    @Override
    @GetMapping("/my-history")
    public List<PurchaseResponseDTO> findMyPurchases(@RequestParam(required = false) StatusPurchase status) {
        return purchaseService.findPurchasesByCurrentUser(status);
    }

    @Override
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PurchaseResponseDTO> findAll() {
        return purchaseService.findAll();
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("@purchaseService.isOwner(#id, authentication.principal.id) or hasRole('ADMIN')")
    public PurchaseResponseDTO findByID(@PathVariable Long id) {
        return purchaseService.findById(id);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("@purchaseService.isOwner(#id, authentication.principal.id) or hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        purchaseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}