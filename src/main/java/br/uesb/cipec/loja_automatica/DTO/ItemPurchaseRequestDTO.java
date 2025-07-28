package br.uesb.cipec.loja_automatica.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ItemPurchaseRequestDTO {
  @NotNull(message =  "Product ID is required")
  private Long productID;
  
  @Min(value = 1, message = "Quantity must be at least 1")
  @Positive
  private Integer quantity;

  
  public ItemPurchaseRequestDTO() {
  }
  
  public Long getProductID() {
    return productID;
  }
  public void setProductID(Long productID) {
    this.productID = productID;
  }
  public Integer getQuantity() {
    return quantity;
  }
  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }
  
}
