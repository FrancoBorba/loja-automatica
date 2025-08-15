package br.uesb.cipec.loja_automatica.controller;

import br.uesb.cipec.loja_automatica.DTO.PurchaseResponseDTO;
import br.uesb.cipec.loja_automatica.controller.docs.PurchaseControllerDocs;
import br.uesb.cipec.loja_automatica.enums.StatusPurchase;
import br.uesb.cipec.loja_automatica.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/purchases") 
public class PurchaseController implements PurchaseControllerDocs {

    @Autowired
    private PurchaseService purchaseService;

    @Override
    @GetMapping("/my-history")
    public ResponseEntity<Page<PurchaseResponseDTO>> findMyPurchases(
      @RequestParam(required = false) StatusPurchase status,
      @RequestParam(value = "page" , defaultValue = "0") Integer page,
      @RequestParam(value = "size" , defaultValue = "10") Integer size,
      @RequestParam(value = "direction" , defaultValue = "desc") String direction
      ) {

       var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;

      Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection,"creationDate"));

        return ResponseEntity.ok(purchaseService.findPurchasesByCurrentUser(status,pageable));
    }

    @Override
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<Page<PurchaseResponseDTO>> findAll(
      @RequestParam(value = "page" , defaultValue = "0") Integer page,
      @RequestParam(value = "size" , defaultValue = "10") Integer size,
      @RequestParam(value = "direction" , defaultValue = "asc") String direction
    ) {

       var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;

      Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection,"creationDate"));

        return ResponseEntity.ok(purchaseService.findAll(pageable));
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