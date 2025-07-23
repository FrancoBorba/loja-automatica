package br.uesb.cipec.loja_automatica.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.uesb.cipec.loja_automatica.enums.StatusCompra;
import br.uesb.cipec.loja_automatica.enums.TypePayment;
import br.uesb.cipec.loja_automatica.model.ItemPurchase;

public class PurchaseDTO {

  private Long id;

  private LocalDateTime creationDate;


  private StatusCompra status;

  private TypePayment payment;

  private BigDecimal value;

  private List<ItemPurchase> itens = new ArrayList<>();

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

  public StatusCompra getStatus() {
    return status;
  }

  public void setStatus(StatusCompra status) {
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

  public List<ItemPurchase> getItens() {
    return itens;
  }

  public void setItens(List<ItemPurchase> itens) {
    this.itens = itens;
  }

}
