package br.uesb.cipec.loja_automatica.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.stripe.Stripe;

import jakarta.annotation.PostConstruct;

@Configuration
public class StripeConfig{

    // Read the value of secret key from yml
    @Value("${stripe.secret-key}")
    private String secretKey;

    @PostConstruct
    public void init(){
        // Sets the key for the Stripe API
       Stripe.apiKey = secretKey;
    }
        
}