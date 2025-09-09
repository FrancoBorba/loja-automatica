package br.uesb.cipec.loja_automatica.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import br.uesb.cipec.loja_automatica.DTO.PurchaseResponseDTO;
import br.uesb.cipec.loja_automatica.model.Purchase;

@Mapper(componentModel = "spring", uses = {ItemPurchaseMapper.class})
public interface PurchaseMapper {

    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "creationDate", target = "creationDate"),
        @Mapping(source = "status", target = "status"),
        @Mapping(source = "payment", target = "payment"),
        @Mapping(source = "value", target = "value"),
        @Mapping(source = "itens", target = "itens")
    })
    PurchaseResponseDTO toResponseDTO(Purchase purchase);
}
/*
The uses = {ItemPurchaseMapper.class} tells MapStruct to use ItemPurchaseMapper internally when mapping List<ItemPurchase> ➔ List<ItemPurchaseResponseDTO>.

All fields are mapped directly (same names, just made explicit for clarification purposes).
 */