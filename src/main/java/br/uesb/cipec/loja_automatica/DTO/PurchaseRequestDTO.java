package br.uesb.cipec.loja_automatica.DTO;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.uesb.cipec.loja_automatica.enums.StatusPurchase;
import br.uesb.cipec.loja_automatica.enums.TypePayment;

public class PurchaseRequestDTO {


  private StatusPurchase statusPurchase;
  

  private TypePayment payment;


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
