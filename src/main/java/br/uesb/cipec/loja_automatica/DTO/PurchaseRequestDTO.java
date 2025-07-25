package br.uesb.cipec.loja_automatica.DTO;


import java.util.List;

import br.uesb.cipec.loja_automatica.enums.StatusPurchase;
import br.uesb.cipec.loja_automatica.enums.TypePayment;

public class PurchaseRequestDTO {
  private StatusPurchase status;  
  private TypePayment payment;
  private List<ItemPurchaseRequestDTO> itens;
  public StatusPurchase getStatus() {
    return status;
  }
  public void setStatus(StatusPurchase status) {
    this.status = status;
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
  
}
