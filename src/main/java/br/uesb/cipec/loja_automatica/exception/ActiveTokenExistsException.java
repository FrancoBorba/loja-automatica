package br.uesb.cipec.loja_automatica.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ActiveTokenExistsException extends RuntimeException{

    public ActiveTokenExistsException(String message) {
        super(message);
    }
    
}
