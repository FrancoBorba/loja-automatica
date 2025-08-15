package br.uesb.cipec.loja_automatica.service;

import br.uesb.cipec.loja_automatica.DTO.PurchaseResponseDTO;
import br.uesb.cipec.loja_automatica.component.AuthenticationFacade;
import br.uesb.cipec.loja_automatica.enums.StatusPurchase;
import br.uesb.cipec.loja_automatica.exception.ResourceNotFoundException;
import br.uesb.cipec.loja_automatica.mapper.PurchaseMapper;
import br.uesb.cipec.loja_automatica.model.Purchase;
import br.uesb.cipec.loja_automatica.model.User;
import br.uesb.cipec.loja_automatica.payment.StripeService;
import br.uesb.cipec.loja_automatica.repository.PurchaseRepository;
import com.stripe.exception.StripeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private PurchaseMapper purchaseMapper;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private StockService stockService;

    @Autowired
    private StripeService stripeService;

    private final Logger logger = LoggerFactory.getLogger(PurchaseService.class.getName());

    // --- MÉTODOS PARA O HISTÓRICO DE COMPRAS (PurchaseController) ---

    @Transactional(readOnly = true)
    public Page<PurchaseResponseDTO> findAll(Pageable pageable) {

        logger.info("Finding all purchases (Admin operation).");

        Page<Purchase> purchases = purchaseRepository.findAll(pageable);

        return purchases.map(purchaseMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public PurchaseResponseDTO findById(Long id) {
        logger.info("Finding purchase by ID: {}", id);
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase with ID " + id + " not found"));
        return purchaseMapper.toResponseDTO(purchase);
    }

    @Transactional(readOnly = true)
    public Page<PurchaseResponseDTO> findPurchasesByCurrentUser(Pageable pageable , StatusPurchase status) {
        logger.info("Finding purchases for current user. Filter status: {}", status);
        User currentUser = authenticationFacade.getCurrentUser();
        Page<Purchase> purchases;
        if (status != null) {
            purchases = purchaseRepository.findByUserIdAndStatus(currentUser.getId(), status , pageable);
        } else {
            purchases = purchaseRepository.findByUserId(currentUser.getId() , pageable);
        }
        return purchases.map(purchaseMapper::toResponseDTO);
    }
    
    @Transactional
    public void delete(Long id) {
        logger.info("Deleting purchase with ID: {}", id);
        User currentUser = authenticationFacade.getCurrentUser();
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase with ID " + id + " not found"));

        if (!purchase.getUser().getId().equals(currentUser.getId())) {
            // No futuro, adicionar a verificação de ADMIN aqui (com @PreAuthorize)
            throw new AccessDeniedException("User is not authorized to delete this purchase.");
        }
        purchaseRepository.delete(purchase);
    }

    // --- METHODS FOR ACTIVE CART (Used by CartController) ---

    @Transactional(readOnly = true)
    public Optional<PurchaseResponseDTO> findActiveCartByUser() {
        logger.info("Finding active cart for the current user.");
        User currentUser = authenticationFacade.getCurrentUser();
        return purchaseRepository
            .findByUserIdAndStatus(currentUser.getId(), StatusPurchase.AGUARDANDO_PAGAMENTO)
            .stream()
            .findFirst()
            .map(purchaseMapper::toResponseDTO);
    }

    @Transactional
    public String checkout() throws StripeException {
        logger.info("Initiating checkout for the active cart.");
        User currentUser = authenticationFacade.getCurrentUser();
        Purchase cartToCheckout = purchaseRepository
            .findByUserIdAndStatus(currentUser.getId(), StatusPurchase.AGUARDANDO_PAGAMENTO)
            .stream().findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("No active cart found to checkout."));

        if (cartToCheckout.getItens().isEmpty()) {
            throw new IllegalStateException("Cannot checkout an empty cart.");
        }
        
        // Create a session of payment
        return stripeService.creatCheckoutSesion(cartToCheckout);
    }


    @Transactional
    public void confirmPayment(Long purchaseId){
         logger.info("Confirm Paymente for the active cart.");

       

         Purchase purchaseToConfirm = purchaseRepository.findById(purchaseId)
         .orElseThrow(() -> new ResourceNotFoundException("No active cart found to checkout."));

    if (purchaseToConfirm.getStatus() == StatusPurchase.AGUARDANDO_PAGAMENTO) {
        
        stockService.debitStock(purchaseToConfirm.getItens());  
        
        purchaseToConfirm.setStatus(StatusPurchase.PAGO);
        purchaseRepository.save(purchaseToConfirm);
        
        logger.info("Purchase {} successfully updated to PAID.", purchaseId);
        
        // TODO: Chamar um NotificationService para enviar e-mail de confirmação. 
    } else {
        logger.warn("Webhook received for a purchase that was not in AGUARDANDO_PAGAMENTO state. Purchase ID: {}. Current status: {}", 
                    purchaseId, purchaseToConfirm.getStatus());
    }
    }

  
   

   
}