package br.uesb.cipec.loja_automatica.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import br.uesb.cipec.loja_automatica.security.UserDetailsImpl;

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
Starting to develop based on the user flow, I changed from create to updateActiveCart since in real applications there is no more than one cart.

   */
     public PurchaseResponseDTO updateActivePurchase(PurchaseRequestDTO requestDTO) {
    logger.info("Creating or updating a purchase (active cart).");

    //  Step 1: Find the currently logged-in user 
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    User currentUser = userDetails.getUser();

    //  Find an existing active cart or create a new one
    Purchase purchase;
    Optional<Purchase> existingCart = purchaseRepository
        .findByUserIdAndStatus(currentUser.getId(), StatusPurchase.AGUARDANDO_PAGAMENTO)
        .stream().findFirst();

    if (existingCart.isPresent()) {
        // If an active cart already exists, use it.
        purchase = existingCart.get();
        logger.info("Found an existing active cart with ID: " + purchase.getId());
    } else {
        // If no active cart exists, create a new one.
        purchase = new Purchase();
        purchase.setUser(currentUser);
        purchase.setStatus(StatusPurchase.AGUARDANDO_PAGAMENTO);
        purchase.setCreationDate(LocalDateTime.now());
        logger.info("No active cart found. Creating a new one for user ID: " + currentUser.getId());
    }

    //  Update the purchase details based on the request 
    purchase.setPayment(requestDTO.getPayment());

    // This logic replaces all items in the cart with the new ones from the request.
    List<ItemPurchase> itens = new ArrayList<>();
    BigDecimal totalValue = BigDecimal.ZERO;

    if (requestDTO.getItens() == null || requestDTO.getItens().isEmpty()) {
        throw new RequiredObjectIsNullException("A purchase must have at least one item.");
    }

    // Process the items from the request
    for (ItemPurchaseRequestDTO itemRequest : requestDTO.getItens()) {
        Product product = productRepository.findById(itemRequest.getProductID())
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + itemRequest.getProductID() + " not found"));

        ItemPurchase item = new ItemPurchase();
        item.setProduct(product);
        item.setQuantity(itemRequest.getQuantity());
        
        BigDecimal subTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
        item.setSubvalor(subTotal);
        item.setPurchase(purchase); // Link the item to the purchase

        totalValue = totalValue.add(subTotal);
        itens.add(item);
    }
    
    // Clear old items and set the new ones
    // Note: For a true "add to cart" feature, you would add to the existing list instead of clearing.
    purchase.getItens().clear(); 
    purchase.getItens().addAll(itens);
    purchase.setValue(totalValue);

    //  Handle stock and save 
    updateStock(requestDTO.getItens());
    Purchase savedPurchase = purchaseRepository.save(purchase);
    
    return purchaseMapper.toResponseDTO(savedPurchase);
}

  public PurchaseResponseDTO updatePurchase(Long id ,PurchaseRequestDTO requestDTO){
     logger.info("Update a purchase");
        //Finding out who the User is by their Token and associating it with a purchase
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = userDetails.getUser();
       

        Purchase existingPurchase = purchaseRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Purchase with ID " + id + " not found"));

      if (!existingPurchase.getUser().getId().equals(currentUser.getId())) {
        throw new AccessDeniedException("User is not authorized to update this purchase.");
    }


// Check if the purchase has already been paid
    if (existingPurchase.getStatus() == StatusPurchase.PAGO) {
        throw new IllegalStateException("Cannot update a purchase that has already been paid.");
    }

      existingPurchase.setPayment(requestDTO.getPayment());


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


public List<PurchaseResponseDTO> findPurchasesByCurrentUser(StatusPurchase status) {
    logger.info("Finding purchases for the current user. Filter status: " + status);

    // Search for the logged in user by token
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    Long currentUserId = userDetails.getUser().getId();

    // Search for user purchases in the database

    List<Purchase> purchases;
    if (status != null) {
        //Search based on filter
        purchases = purchaseRepository.findByUserIdAndStatus(currentUserId, status);
    } else {
        // If you don't use any filter, it returns all purchase
        purchases = purchaseRepository.findByUserId(currentUserId);
    }

    
    return purchases.stream()
            .map(purchaseMapper::toResponseDTO) // Para cada 'purchase', aplica o método de mapeamento
            .toList(); // Coleta os resultados em uma nova lista
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
