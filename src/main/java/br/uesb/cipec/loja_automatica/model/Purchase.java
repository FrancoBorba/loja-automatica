package br.uesb.cipec.loja_automatica.model;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.uesb.cipec.loja_automatica.enums.StatusPurchase;
import br.uesb.cipec.loja_automatica.enums.TypePayment;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "purchase") // I don't know what the best translation for Compra is.
public class Purchase {
  public Purchase(){}

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "data_criacao", nullable = false) // the name here need be equal the name in migration
  private LocalDateTime creationDate;

  @Column(nullable = false) // for default is true
  @Enumerated(EnumType.STRING) // converts the enumeration value to its string representation
  private StatusPurchase status;

  @Column(nullable = false , name = "tipo_pagamento") // for default is true
  @Enumerated(EnumType.STRING)
  private TypePayment payment;

  @Column( precision = 5 , scale = 2 ,nullable = false , name ="valor") // The price format will be xxx.yy
  private BigDecimal value;

  @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
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

  public List<ItemPurchase> getItens() {
    return itens;
  }

  public void setItens(List<ItemPurchase> itens) {
    this.itens = itens;
  }

  @PrePersist // indicates that a method must be executed before an entity is persisted (saved) to the database
  public void prePersist(){
    this.creationDate = LocalDateTime.now();
  }

  @Override
  public String toString() {
    return "Purchase [id=" + id + ", creationDate=" + creationDate + ", status=" + status + ", payment=" + payment
        + ", value=" + value + ", itens=" + itens + "]";
  }
  
}
