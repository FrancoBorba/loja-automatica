package br.uesb.cipec.loja_automatica.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class EmailSendingException extends RuntimeException {

    public EmailSendingException(String message) {
        super(message);
    }
    
}
