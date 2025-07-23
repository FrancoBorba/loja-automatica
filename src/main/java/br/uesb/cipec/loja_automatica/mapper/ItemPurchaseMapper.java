package br.uesb.cipec.loja_automatica.mapper;

import org.mapstruct.Mapper;

import br.uesb.cipec.loja_automatica.DTO.ItemPurchaseDTO;
import br.uesb.cipec.loja_automatica.model.ItemPurchase;

@Mapper(componentModel = "spring")
public interface ItemPurchaseMapper {
  
  public ItemPurchaseDTO toDTO(ItemPurchase itemPurchase);

  public ItemPurchase toEntity(ItemPurchaseDTO itemPurchase);
}
