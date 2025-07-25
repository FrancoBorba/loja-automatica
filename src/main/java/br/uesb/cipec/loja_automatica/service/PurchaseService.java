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
import br.uesb.cipec.loja_automatica.exception.RequiredObjectIsNullException;
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

  /*
MapStruct isn't as efficient for Request ➔ Entity when we need additional logic (such as calculating values or searching the database).
Therefore, for Request ➔ Entity, it's recommended to manually map in the Service, for example:
   */  
 
      public PurchaseResponseDTO createPurchase(PurchaseRequestDTO requestDTO) {
         System.out.println("DTO Recebido no Controller: " + requestDTO.toString());
        Purchase purchase = new Purchase();
        //Take this data from the request and set it for the response
        purchase.setStatus(requestDTO.getStatusPurchase());
        purchase.setPayment(requestDTO.getPayment());
        // Take the data of criation
        purchase.setCreationDate(LocalDateTime.now());

        List<ItemPurchase> itens = new ArrayList<>();
        BigDecimal totalValue = BigDecimal.ZERO; // start the total value with 0

        if (requestDTO.getItens() == null || requestDTO.getItens().isEmpty()) {
    throw new RequiredObjectIsNullException("A purchase must have at least one item.");
}

        // get the itens of request 
         for (ItemPurchaseRequestDTO itemRequest : requestDTO.getItens()) {
          // We cannot use the findByID method of the class because it returns the PurchaseResponseDTO
        Product product = productRepository.findById(itemRequest.getProductID())
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + itemRequest.getProductID() + " not found"));

        ItemPurchase item = new ItemPurchase();

        item.setProduct(product); // Add the product
        item.setQuantity(itemRequest.getQuantity()); // Get the quantity of items in the product

        
        BigDecimal subTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
        item.setSubvalor(subTotal);
        item.setPurchase(purchase);

        totalValue = totalValue.add(subTotal);
        itens.add(item);

         }
                 purchase.setItens(itens);
        purchase.setValue(totalValue);

        Purchase savedPurchase = purchaseRepository.save(purchase);
        return purchaseMapper.toResponseDTO(savedPurchase);
      }






}
