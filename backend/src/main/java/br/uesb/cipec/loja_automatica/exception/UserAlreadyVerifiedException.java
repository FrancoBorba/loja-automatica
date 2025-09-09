package br.uesb.cipec.loja_automatica.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyVerifiedException extends RuntimeException{

    public UserAlreadyVerifiedException(String message) {
        super(message);
    }

}
