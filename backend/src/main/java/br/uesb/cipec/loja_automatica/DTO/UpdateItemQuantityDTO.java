package br.uesb.cipec.loja_automatica.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
@Schema(description = "Quantity of item")
public class UpdateItemQuantityDTO {

    @NotNull(message = "New quantity is required.")
    @Min(value = 0, message = "Quantity cannot be negative.")
    @Schema(description = "The new quantity of a product" , example = "4")
    private Integer newQuantity;

    // Construtor vazio
    public UpdateItemQuantityDTO() {}

    // Getters e Setters
    public Integer getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(Integer newQuantity) {
        this.newQuantity = newQuantity;
    }
}