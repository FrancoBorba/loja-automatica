package br.uesb.cipec.loja_automatica.DTO;


import java.util.List;

import br.uesb.cipec.loja_automatica.enums.StatusPurchase;
import br.uesb.cipec.loja_automatica.enums.TypePayment;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO for creating a purchase")
public class PurchaseRequestDTO {


  @NotNull(message = "Payment is mandatory")
    @Schema(description = "Type of payment", example = "DEBITO")
  private TypePayment payment;
 
  @Schema(description = "Item lists")
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


  @Override
  public String toString() {
    return "PurchaseRequestDTO [statusPurchase=" +", payment=" + payment + ", itens=" + itens + "]";
  }
  
}
