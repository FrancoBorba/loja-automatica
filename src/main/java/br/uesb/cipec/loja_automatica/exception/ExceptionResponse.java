package br.uesb.cipec.loja_automatica.exception;

import java.util.Date;

/*
 *This class standardizes the JSON returned on errors.
The format will be:
{
"status": 404,
"message": "Product not found with id: 10",
"timestamp": "2025-07-17T14:32:00"
}
 */
public class ExceptionResponse {
    private Date timestamp; 
    private String message;
    private String details;
    
   

    public ExceptionResponse(Date timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }

     public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }
}
