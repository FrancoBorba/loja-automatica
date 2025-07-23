package br.uesb.cipec.loja_automatica.DTO;

import java.math.BigDecimal;



public class ItemPurchaseDTO {
/*
In this DTO we will not expose Buy to avoid infinite recursion
and we will only expose the name and price of the product not the entire object.
 */
    private Long id;

    private String productName; 
    private BigDecimal productPrice;

    private BigDecimal valor;
    private Integer quantity;


}
