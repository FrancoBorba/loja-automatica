package br.uesb.cipec.loja_automatica.DTO;


import java.util.List;

import br.uesb.cipec.loja_automatica.enums.StatusPurchase;
import br.uesb.cipec.loja_automatica.enums.TypePayment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class PurchaseRequestDTO {

  @NotNull(message = "Purchase status is required")
  private StatusPurchase statusPurchase;
  
  
  @NotNull(message = "Payment is mandatory")
  private TypePayment payment;


  @NotEmpty(message = "The item List cannot be empty")
  @Valid // Validates the items since it is internal
  private List<ItemPurchaseRequestDTO> itens;
  

  public PurchaseRequestDTO() {
  }

  public TypePayment getPayment() {
    return payment;
  }
  public void setPayment(TypePayment payment) {
    this.payment = payment;
  }
  public List<ItemPurchaseRequestDTO> getItens() {
    return itens;
  }
  public void setItens(List<ItemPurchaseRequestDTO> itens) {
    this.itens = itens;
  }
  public StatusPurchase getStatusPurchase() {
    return statusPurchase;
  }
  public void setStatusPurchase(StatusPurchase statusPurchase) {
    this.statusPurchase = statusPurchase;
  }
  @Override
  public String toString() {
    return "PurchaseRequestDTO [statusPurchase=" + statusPurchase + ", payment=" + payment + ", itens=" + itens + "]";
  }
  
}
