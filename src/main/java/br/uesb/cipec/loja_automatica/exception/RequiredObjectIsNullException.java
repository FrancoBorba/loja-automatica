package br.uesb.cipec.loja_automatica.exception;

public class RequiredObjectIsNullException extends RuntimeException{
    public RequiredObjectIsNullException(String message){
        super(message);
    }
}
