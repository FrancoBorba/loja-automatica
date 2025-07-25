package br.uesb.cipec.loja_automatica.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.uesb.cipec.loja_automatica.DTO.ItemPurchaseRequestDTO;
import br.uesb.cipec.loja_automatica.DTO.PurchaseRequestDTO;
import br.uesb.cipec.loja_automatica.DTO.PurchaseResponseDTO;
import br.uesb.cipec.loja_automatica.exception.ResourceNotFoundException;
import br.uesb.cipec.loja_automatica.mapper.PurchaseMapper;
import br.uesb.cipec.loja_automatica.model.ItemPurchase;
import br.uesb.cipec.loja_automatica.model.Product;
import br.uesb.cipec.loja_automatica.model.Purchase;
import br.uesb.cipec.loja_automatica.repository.ProductRepository;
import br.uesb.cipec.loja_automatica.repository.PurchaseRepository;

@Service
public class PurchaseService {

  @Autowired
  ProductRepository productRepository;

  @Autowired
   PurchaseRepository purchaseRepository;

   @Autowired
   PurchaseMapper purchaseMapper;

  public PurchaseResponseDTO findById(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Purchase with ID " + id + " not found"));

      var responseDTO = purchaseMapper.toResponseDTO(purchase);
      return responseDTO;
    }

      public PurchaseResponseDTO createPurchase(PurchaseRequestDTO requestDTO) {
        return null;
      }




  /*
MapStruct isn't as efficient for Request ➔ Entity when we need additional logic (such as calculating values or searching the database).
Therefore, for Request ➔ Entity, it's recommended to manually map in the Service, for example:
   */  
  public Purchase createPurchaseFromRequest(PurchaseRequestDTO dto) {
    Purchase purchase = new Purchase();
    purchase.setStatus(dto.getStatus());
    purchase.setPayment(dto.getPayment());
    purchase.setCreationDate(LocalDateTime.now());

    List<ItemPurchase> itens = new ArrayList<>();
    BigDecimal total = BigDecimal.ZERO;

    for (ItemPurchaseRequestDTO itemDTO : dto.getItens()) {
        Product product = productRepository.findById(itemDTO.getProductID())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        ItemPurchase item = new ItemPurchase();
        item.setProduct(product);
        item.setQuantity(itemDTO.getQuantity());
        BigDecimal subValue = product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
        item.setSubvalor(subValue);
        item.setPurchase(purchase);

        total = total.add(subValue);
        itens.add(item);
    }

    purchase.setItens(itens);
    purchase.setValue(total);

    return purchase;
}

}
