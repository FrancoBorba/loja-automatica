package br.uesb.cipec.loja_automatica.DTO;

import java.math.BigDecimal;

public class ItemPurchaseResponseDTO {
   private Long id;
   private Integer quantity;
   private BigDecimal value;
   private String productName;
   
   public ItemPurchaseResponseDTO() {
  }
  
   public Long getId() {
     return id;
   }
   public void setId(Long id) {
     this.id = id;
   }
   public Integer getQuantity() {
     return quantity;
   }
   public void setQuantity(Integer quantity) {
     this.quantity = quantity;
   }
   public BigDecimal getValue() {
     return value;
   }
   public void setValue(BigDecimal value) {
     this.value = value;
   }
   public String getProductName() {
     return productName;
   }
   public void setProductName(String productName) {
     this.productName = productName;
   }
}
