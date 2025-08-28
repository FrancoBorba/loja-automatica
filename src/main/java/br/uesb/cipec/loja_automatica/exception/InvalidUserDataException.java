package br.uesb.cipec.loja_automatica.exception;

public class InvalidUserDataException extends RuntimeException {
  
  public InvalidUserDataException(String message){
    super(message);
  }
}
