package br.uesb.cipec.loja_automatica.DTO;



public class ItemPurchaseRequestDTO {
  private Long productID;
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
