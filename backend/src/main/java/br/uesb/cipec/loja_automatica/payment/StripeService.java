package br.uesb.cipec.loja_automatica.payment;

import br.uesb.cipec.loja_automatica.model.ItemPurchase;
import br.uesb.cipec.loja_automatica.model.Purchase;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;


import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class StripeService {
    
    public String creatCheckoutSesion(Purchase cart) throws StripeException{
       
        // Create success and cancellation URLs
        String successUrl = "http://localhost:8080/payment-success.html"; 
        String cancelUrl = "http://localhost:8080/payment-cancel.html";  

        //Array list to add items in the format Stripe understands
        List<SessionCreateParams.LineItem> lineItems = new ArrayList();

        // Going through the items and adapting for Stripe
       for (ItemPurchase item : cart.getItens()) {
            lineItems.add(
                SessionCreateParams.LineItem.builder()
                    .setQuantity(Long.valueOf(item.getQuantity()))
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("brl")
                            .setUnitAmount(item.getProduct().getPrice().movePointRight(2).longValue())
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(item.getProduct().getName())
                                    .build()
                            )
                            .build()
                    )
                    .build()
            );
        }

        SessionCreateParams param = SessionCreateParams.builder()
        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD) // Only pix
        .setMode(SessionCreateParams.Mode.PAYMENT)// Only payments
        .setSuccessUrl(successUrl)
        .setCancelUrl(cancelUrl) 
        .addAllLineItem(lineItems)
        .putMetadata("purchaseId", cart.getId().toString())
        .setCustomerEmail(cart.getUser().getEmail())
        .build();

        Session session = Session.create(param);
        
        return session.getUrl();
    }
}
