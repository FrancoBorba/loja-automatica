package br.uesb.cipec.loja_automatica.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

import br.uesb.cipec.loja_automatica.service.PurchaseService;

@RestController
@RequestMapping("/api/webhooks")
public class WebHookController {
    
  private final Logger logger = LoggerFactory.getLogger(WebHookController.class);

  @Value("${stripe.webhook-secret}")
  private String webhookSecret;

  @Autowired
  PurchaseService purchaseService;

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        
        Event event;

        try {
            //Check if the notification actually came from Stripe (security)
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            logger.warn("Webhook error while validating signature.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

       // Handles the successful checkout event
        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            
            if (session != null) {
              
                String purchaseIdStr = session.getMetadata().get("purchaseId");
                if (purchaseIdStr != null) {
                    Long purchaseId = Long.parseLong(purchaseIdStr);
                    logger.info("Webhook received for confirmed payment of purchase ID: {}", purchaseId);
                    
            
                    purchaseService.confirmPayment(purchaseId);
                }
            }
        }

        return ResponseEntity.ok().build();
    }

}
