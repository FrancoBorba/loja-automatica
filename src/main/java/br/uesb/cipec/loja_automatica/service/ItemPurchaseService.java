package br.uesb.cipec.loja_automatica.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante para garantir a consistência

import br.uesb.cipec.loja_automatica.DTO.ItemPurchaseRequestDTO;
import br.uesb.cipec.loja_automatica.DTO.PurchaseResponseDTO;
import br.uesb.cipec.loja_automatica.component.AuthenticationFacade;
import br.uesb.cipec.loja_automatica.enums.StatusPurchase;
import br.uesb.cipec.loja_automatica.enums.TypePayment;
import br.uesb.cipec.loja_automatica.exception.InvalidPurchaseQuantityException;
import br.uesb.cipec.loja_automatica.exception.RequiredObjectIsNullException;
import br.uesb.cipec.loja_automatica.exception.ResourceNotFoundException;
import br.uesb.cipec.loja_automatica.mapper.PurchaseMapper;
import br.uesb.cipec.loja_automatica.model.ItemPurchase;
import br.uesb.cipec.loja_automatica.model.Product;
import br.uesb.cipec.loja_automatica.model.Purchase;
import br.uesb.cipec.loja_automatica.model.User;
import br.uesb.cipec.loja_automatica.repository.ProductRepository;
import br.uesb.cipec.loja_automatica.repository.PurchaseRepository;

@Service
public class ItemPurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PurchaseMapper purchaseMapper;
    @Autowired
    private AuthenticationFacade facade;

    @Transactional
    public PurchaseResponseDTO addItemToCart(ItemPurchaseRequestDTO itemRequest) {
        // 1. Validação de objeto nulo
        if (itemRequest == null) {
            throw new RequiredObjectIsNullException("The item to be added to the cart cannot be null.");
        }
        // 2. Validação de quantidade
        if (itemRequest.getQuantity() <= 0) {
            throw new InvalidPurchaseQuantityException("The quantity of an item to be added must be greater than zero.");
        }

        Purchase activeCart = getOrCreateActiveCartForCurrentUser();

        Optional<ItemPurchase> existingItemOpt = activeCart.getItens().stream()
                .filter(item -> item.getProduct().getId().equals(itemRequest.getProductID()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            ItemPurchase itemToUpdate = existingItemOpt.get();
            itemToUpdate.setQuantity(itemToUpdate.getQuantity() + itemRequest.getQuantity());
        
            recalculateItemSubtotal(itemToUpdate);
        } else {
            Product product = productRepository.findById(itemRequest.getProductID())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            
            ItemPurchase newItem = new ItemPurchase();
            newItem.setProduct(product);
            newItem.setQuantity(itemRequest.getQuantity());
            newItem.setPurchase(activeCart);
          
            recalculateItemSubtotal(newItem);

            activeCart.getItens().add(newItem);
        }

      
        recalculatePurchaseTotal(activeCart);
    
        Purchase savedCart = purchaseRepository.save(activeCart);
        
        return purchaseMapper.toResponseDTO(savedCart);
    }

    @Transactional
    public PurchaseResponseDTO removeItemFromCart(Long itemIdToRemove) {
        Purchase activeCart = getActiveCartForCurrentUser();

        ItemPurchase itemToRemove = activeCart.getItens().stream()
                .filter(item -> item.getId().equals(itemIdToRemove))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item with ID " + itemIdToRemove + " not found in the cart."));

        activeCart.getItens().remove(itemToRemove);

        recalculatePurchaseTotal(activeCart);
        
        Purchase savedCart = purchaseRepository.save(activeCart);
        
        return purchaseMapper.toResponseDTO(savedCart);
    }

    @Transactional
    public PurchaseResponseDTO updateItemQuantity(Long itemId, int newQuantity) {
        Purchase activeCart = getActiveCartForCurrentUser();

        ItemPurchase itemToUpdate = activeCart.getItens().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item with ID " + itemId + " not found in the cart."));

        if (newQuantity > 0) {
            itemToUpdate.setQuantity(newQuantity);
            recalculateItemSubtotal(itemToUpdate);
        } else {
            activeCart.getItens().remove(itemToUpdate);
        }

        recalculatePurchaseTotal(activeCart);
        
        Purchase savedCart = purchaseRepository.save(activeCart);
        
        return purchaseMapper.toResponseDTO(savedCart);
    }



    private Purchase getOrCreateActiveCartForCurrentUser() {
        User currentUser = facade.getCurrentUser();
        return purchaseRepository.findByUserIdAndStatus(currentUser.getId(), StatusPurchase.AGUARDANDO_PAGAMENTO)
                .stream().findFirst().orElseGet(() -> {
                    Purchase newCart = new Purchase();
                    newCart.setUser(currentUser);
                    newCart.setStatus(StatusPurchase.AGUARDANDO_PAGAMENTO);
                    newCart.setPayment(TypePayment.PIX); 
                    newCart.setValue(BigDecimal.ZERO);  
                    return purchaseRepository.save(newCart);
                });
    }

    private Purchase getActiveCartForCurrentUser() {
        User currentUser = facade.getCurrentUser();
        return purchaseRepository.findByUserIdAndStatus(currentUser.getId(), StatusPurchase.AGUARDANDO_PAGAMENTO)
                .stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No active cart found for the user."));
    }

    private void recalculateItemSubtotal(ItemPurchase item) {
        if (item == null || item.getProduct() == null) return;
        BigDecimal subTotal = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        item.setSubvalor(subTotal);
    }

    private void recalculatePurchaseTotal(Purchase purchase) {
        BigDecimal totalValue = purchase.getItens().stream()
                .map(ItemPurchase::getSubvalor) // Agora podemos somar os subtotais que já estão corretos
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        purchase.setValue(totalValue);
    }
}