package br.uesb.cipec.loja_automatica.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import br.uesb.cipec.loja_automatica.DTO.ItemPurchaseResponseDTO;
import br.uesb.cipec.loja_automatica.model.ItemPurchase;

@Mapper(componentModel = "spring")
public interface ItemPurchaseMapper {

    @Mappings({
        @Mapping(source = "product.name", target = "productName"),
        @Mapping(source = "quantity", target = "quantity"),
        @Mapping(source = "subvalor", target = "value"),
        @Mapping(source = "id", target = "id")
    })
    ItemPurchaseResponseDTO toResponseDTO(ItemPurchase itemPurchase);
}
/*
Maps From ItemPurchase(Source) to ItemPurchaseResponseDTO(target) if the fields are equal it is unecessary

 */
