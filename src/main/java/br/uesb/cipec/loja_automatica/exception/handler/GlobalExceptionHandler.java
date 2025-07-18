package br.uesb.cipec.loja_automatica.exception.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.uesb.cipec.loja_automatica.exception.ExceptionResponse;
import br.uesb.cipec.loja_automatica.exception.RequiredObjectIsNullException;
import br.uesb.cipec.loja_automatica.exception.ResourceNotFoundException;

@ControllerAdvice // Interprets all exceptions thrown in the controllers
@RestController // Indicates that the return is in json
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  /*
   * This method catches 
   * any exception handling not done by us 
   * and returns the json with the 500 error
   */
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> handleAllException(Exception exception ,WebRequest request){
        ExceptionResponse response = new ExceptionResponse(new Date(),
         exception.getMessage(), request.getDescription(false));

         return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /*
     * This method catches any status code 400 
     * error and returns the appropriate json  
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> 
    handleResourceNotFoundExcpetion(ResourceNotFoundException exception , WebRequest request){
        ExceptionResponse response = new ExceptionResponse(new Date()
        , exception.getMessage(), request.getDescription(false));

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /*
     * This method catches any status code 400 
     * error and returns the appropriate json  
     */
    @ExceptionHandler(RequiredObjectIsNullException.class)
    public ResponseEntity<ExceptionResponse> handleRequiredObjectIsNullException(RequiredObjectIsNullException ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
                new Date(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handler para erros de validação de DTOs (@Valid) sem conflito.
     * Não está funcioando , se tentar inserir ou dar update em um JSON 
     *  null retorna o erro 500 o genérico , depois consertar
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        ExceptionResponse response = new ExceptionResponse(
                new Date(),
                "Validation failed",
                errors.toString()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
