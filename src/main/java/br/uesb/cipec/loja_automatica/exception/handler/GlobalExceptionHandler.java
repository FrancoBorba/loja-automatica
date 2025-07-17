package br.uesb.cipec.loja_automatica.exception.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice // Interprets all exceptions thrown in the controllers
@RestController // Indicates that the return is in json
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
}
