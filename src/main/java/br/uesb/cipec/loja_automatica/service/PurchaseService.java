package br.uesb.cipec.loja_automatica.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.uesb.cipec.loja_automatica.DTO.ItemPurchaseRequestDTO;
import br.uesb.cipec.loja_automatica.DTO.PurchaseRequestDTO;
import br.uesb.cipec.loja_automatica.DTO.PurchaseResponseDTO;
import br.uesb.cipec.loja_automatica.enums.StatusPurchase;
import br.uesb.cipec.loja_automatica.exception.RequiredObjectIsNullException;
import br.uesb.cipec.loja_automatica.exception.ResourceNotFoundException;
import br.uesb.cipec.loja_automatica.mapper.PurchaseMapper;
import br.uesb.cipec.loja_automatica.model.ItemPurchase;
import br.uesb.cipec.loja_automatica.model.Product;
import br.uesb.cipec.loja_automatica.model.Purchase;
import br.uesb.cipec.loja_automatica.model.User;
import br.uesb.cipec.loja_automatica.repository.ProductRepository;
import br.uesb.cipec.loja_automatica.repository.PurchaseRepository;
import br.uesb.cipec.loja_automatica.repository.UserRepository;

@Service
public class PurchaseService {

  @Autowired
  ProductRepository productRepository;

  @Autowired
   PurchaseRepository purchaseRepository;

   @Autowired
   UserRepository userRepository;

   @Autowired
   PurchaseMapper purchaseMapper;

       // For adding loggers in he applicaiton
    // We will use the logs at the info level here
    private Logger logger = LoggerFactory.getLogger(ProductService.class.getName());

    public List<PurchaseResponseDTO> findAll(){
      logger.info("Find all Purchase");

      var entity = purchaseRepository.findAll();
      List<PurchaseResponseDTO> responseDTOs = new ArrayList<>();

      for(Purchase response : entity){
        responseDTOs.add(purchaseMapper.toResponseDTO(response));
      }
  return responseDTOs;
    }

  public PurchaseResponseDTO findById(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Purchase with ID " + id + " not found"));

      logger.info("Find a purchase by ID");

      var responseDTO = purchaseMapper.toResponseDTO(purchase);
      return responseDTO;
    }

  /*
MapStruct isn't as efficient for Request ➔ Entity when we need additional logic (such as calculating values or searching the database).
Therefore, for Request ➔ Entity, it's recommended to manually map in the Service, for example:
   */  
 
      public PurchaseResponseDTO createPurchase(PurchaseRequestDTO requestDTO) {
         logger.info("Create a purchase");
        Purchase purchase = new Purchase();
        //Take this data from the request and set it for the response
        purchase.setStatus(requestDTO.getStatusPurchase());
        purchase.setPayment(requestDTO.getPayment());
        // Take the data of criation
        purchase.setCreationDate(LocalDateTime.now());

        User user = userRepository.findById(requestDTO.getUserID()).orElseThrow(
          () -> new ResourceNotFoundException("User with ID " + requestDTO.getUserID() + " not found"));

          purchase.setUser(user);

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

       updateStock(requestDTO.getItens());
        Purchase savedPurchase = purchaseRepository.save(purchase);
        return purchaseMapper.toResponseDTO(savedPurchase);
      }

  public PurchaseResponseDTO updatePurchase(Long id ,PurchaseRequestDTO requestDTO){
     logger.info("Update a purchase");
        Purchase existingPurchase = purchaseRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Purchase with ID " + id + " not found"));


// Check if the purchase has already been paid
    if (existingPurchase.getStatus() == StatusPurchase.PAGO) {
        throw new IllegalStateException("Cannot update a purchase that has already been paid.");
    }

      existingPurchase.setPayment(requestDTO.getPayment());
      existingPurchase.setStatus(requestDTO.getStatusPurchase());

  
    //Clean the old itens
    existingPurchase.getItens().clear();
    BigDecimal totalValue = BigDecimal.ZERO;

        if (requestDTO.getItens() == null || requestDTO.getItens().isEmpty()) {
        throw new RequiredObjectIsNullException("A purchase must have at least one item.");
    }

    for (ItemPurchaseRequestDTO itemRequest : requestDTO.getItens()) {
        Product product = productRepository.findById(itemRequest.getProductID())
            .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + itemRequest.getProductID() + " not found"));

        ItemPurchase item = new ItemPurchase();
        item.setProduct(product);
        item.setQuantity(itemRequest.getQuantity());

        BigDecimal subTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
        item.setSubvalor(subTotal);
        item.setPurchase(existingPurchase);

        totalValue = totalValue.add(subTotal);
        existingPurchase.getItens().add(item);
    }

    existingPurchase.setValue(totalValue);

  
     updateStock(requestDTO.getItens());
    Purchase updatedPurchase = purchaseRepository.save(existingPurchase);
    return purchaseMapper.toResponseDTO(updatedPurchase);

      }

      public void delete(Long id){
         logger.info("Delete a purchase");
        Purchase purchase = purchaseRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Purchase with ID " + id + " not found"));

        purchaseRepository.delete(purchase);
      }




private void updateStock(List<ItemPurchaseRequestDTO> items) {
    for (ItemPurchaseRequestDTO item : items) {
        Product product = productRepository.findById(item.getProductID())
                .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductID()));

        if (product.getAmount() < item.getQuantity()) {
            throw new RuntimeException("Insufficient stock for the product:" + product.getName());
        }

        product.setAmount(product.getAmount() - item.getQuantity());
        productRepository.save(product);
    }
}

}
