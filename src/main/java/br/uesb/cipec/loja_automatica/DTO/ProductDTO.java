package br.uesb.cipec.loja_automatica.DTO;

import java.math.BigDecimal;


public class ProductDTO {


    public ProductDTO(){} // Constructor

    private Long id;
    private BigDecimal price;
    private String name;
    private int amount;
    private String description;
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((price == null) ? 0 : price.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + amount;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        return result;
    }

   
    
    

    
}
