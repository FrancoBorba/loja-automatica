package br.uesb.cipec.loja_automatica.mapper;

import org.mapstruct.Mapper;


import br.uesb.cipec.loja_automatica.DTO.PurchaseDTO;
import br.uesb.cipec.loja_automatica.model.Purchase;

// Interface com o mapper
@Mapper(componentModel = "spring")
public  interface PurchaseMapper {
  
    public PurchaseDTO toDTO(Purchase purchase);

    public Purchase toEntity(PurchaseDTO purchase);
   
}
