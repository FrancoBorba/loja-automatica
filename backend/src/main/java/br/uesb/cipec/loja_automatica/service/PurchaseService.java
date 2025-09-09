package br.uesb.cipec.loja_automatica.service;

import br.uesb.cipec.loja_automatica.DTO.PurchaseResponseDTO;
import br.uesb.cipec.loja_automatica.component.AuthenticationFacade;
import br.uesb.cipec.loja_automatica.enums.StatusPurchase;
import br.uesb.cipec.loja_automatica.exception.*;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
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

    // --- METHODS FOR PURCHASE HISTORY (PurchaseController) ---

    @Transactional(readOnly = true)
    public Page<PurchaseResponseDTO> findAll(Pageable pageable) {
        logger.info("Finding all purchases (Admin operation).");
        return purchaseRepository.findAll(pageable).map(purchaseMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public PurchaseResponseDTO findById(Long id) {
        logger.info("Finding purchase by ID: {}", id);
        Purchase purchase = findPurchaseById(id);
        return purchaseMapper.toResponseDTO(purchase);
    }

    @Transactional(readOnly = true)
    public Page<PurchaseResponseDTO> findPurchasesByCurrentUser(StatusPurchase status, Pageable pageable) {
        logger.info("Finding purchases for current user. Filter status: {}", status);
        User currentUser = authenticationFacade.getCurrentUser();
        Page<Purchase> purchasePage;
        if (status != null) {
            purchasePage = purchaseRepository.findByUserIdAndStatus(currentUser.getId(), status, pageable);
        } else {
            purchasePage = purchaseRepository.findByUserId(currentUser.getId(), pageable);
        }
        return purchasePage.map(purchaseMapper::toResponseDTO);
    }
    
    @Transactional
    public void delete(Long id) {
        logger.info("Deleting purchase with ID: {}", id);
        Purchase purchase = findPurchaseById(id);
        purchaseRepository.delete(purchase);
    }

    // METHODS FOR ACTIVE CART (Used by CartController)


    @Transactional(readOnly = true)
    public Optional<PurchaseResponseDTO> findActiveCartByUser() {
        logger.info("Finding active cart for the current user.");
        User currentUser = authenticationFacade.getCurrentUser();

        return purchaseRepository
            .findByUserIdAndStatus(currentUser.getId(), StatusPurchase.AGUARDANDO_PAGAMENTO)
            .map(purchaseMapper::toResponseDTO);
    }

    @Transactional
    public String checkout() throws StripeException {
        logger.info("Initiating checkout for the active cart.");
        
       
        Purchase cartToCheckout = purchaseRepository
            .findByUserIdAndStatus(authenticationFacade.getCurrentUser().getId(), StatusPurchase.AGUARDANDO_PAGAMENTO)
            .orElseThrow(() -> new ResourceNotFoundException("No active cart found to checkout."));

        if (cartToCheckout.getItens().isEmpty()) {
            throw new EmptyCartException("Cannot checkout an empty cart.");
        }
        
        return stripeService.creatCheckoutSesion(cartToCheckout);
    }

    // METHOD CALLED BY WEBHOOK

    @Transactional
    public void confirmPayment(Long purchaseId) {
        logger.info("Confirming payment for purchase ID: {}", purchaseId);
        Purchase purchase = findPurchaseById(purchaseId);

        if (purchase.getStatus() == StatusPurchase.AGUARDANDO_PAGAMENTO) {
            stockService.debitStock(purchase.getItens());
            purchase.setStatus(StatusPurchase.PAGO);
            purchaseRepository.save(purchase);
            logger.info("Purchase {} successfully updated to PAID.", purchaseId);
        } else {
             throw new InvalidPurchaseStatusException("Purchase is not in AGUARDANDO_PAGAMENTO state.");
        }
    }

    //  SUPPORT METHODS

 

    private Purchase findPurchaseById(Long id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with ID: " + id));
    }
}