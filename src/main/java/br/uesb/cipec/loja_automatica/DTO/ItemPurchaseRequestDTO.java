package br.uesb.cipec.loja_automatica.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


@Schema(description = "Purchase item")
public class ItemPurchaseRequestDTO {
  @NotNull(message =  "Product ID is required")
    @Schema(description = "Product ID", example = "3")
  private Long productID;
  
  @Min(value = 1, message = "Quantity must be at least 1")
  @Schema(description = "Quantity purchased" , example = "2")
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
