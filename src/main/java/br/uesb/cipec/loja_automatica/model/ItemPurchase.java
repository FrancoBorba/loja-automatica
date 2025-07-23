package br.uesb.cipec.loja_automatica.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "item_compra")
public class ItemPurchase {
  
  @Id // primary key 
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne // Many item_compra for one product
  @JoinColumn(name = "product_id")
  private Product product;

  @ManyToOne // Many item_compra for one Compra
  @JoinColumn(name = "purchase_id") // Indicates which column will be used as a foreign key in the item_compra table
  /*
Creates a product_id column in the item_purchase table.

This column will store the Product ID associated with each ItemPurchase.
   */
  private Purchase purchase;

  
  @Column( precision = 5 , scale = 2 ,nullable = false) // The price format will be xxx.yy
  private BigDecimal valor;

  @Column(nullable = false)
  private Integer quantity;



  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public Purchase getCompra() {
    return purchase;
  }

  public void setCompra(Purchase purchase) {
    this.purchase = purchase;
  }

  public BigDecimal getValor() {
    return valor;
  }

  public void setValor(BigDecimal valor) {
    this.valor = valor;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }
}
