package br.uesb.cipec.loja_automatica.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InvalidPurchaseStatusException extends RuntimeException{

    public InvalidPurchaseStatusException(String message) {
        super(message);
    }
    
}
