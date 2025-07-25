 package br.uesb.cipec.loja_automatica.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.uesb.cipec.loja_automatica.enums.StatusPurchase;
import br.uesb.cipec.loja_automatica.enums.TypePayment;

public class PurchaseResponseDTO {

  private Long id;
  private LocalDateTime creationDate;
  private StatusPurchase status;
  private TypePayment payment;
  private BigDecimal value;
  private List<ItemPurchaseResponseDTO> itens;
  
  public PurchaseResponseDTO() {
  }
  
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public LocalDateTime getCreationDate() {
    return creationDate;
  }
  public void setCreationDate(LocalDateTime creationDate) {
    this.creationDate = creationDate;
  }
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
  public BigDecimal getValue() {
    return value;
  }
  public void setValue(BigDecimal value) {
    this.value = value;
  }
  public List<ItemPurchaseResponseDTO> getItens() {
    return itens;
  }
  public void setItens(List<ItemPurchaseResponseDTO> itens) {
    this.itens = itens;
  }
  
}
