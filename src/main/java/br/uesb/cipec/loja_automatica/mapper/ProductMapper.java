package br.uesb.cipec.loja_automatica.mapper;

import org.mapstruct.Mapper;

import br.uesb.cipec.loja_automatica.DTO.ProductDTO;
import br.uesb.cipec.loja_automatica.model.Product;

// Interface com o mapper
@Mapper(componentModel = "spring")
public  interface ProductMapper {
 
  public ProductDTO toDTO(Product product);

  public Product toEntity(ProductDTO product);
   
} 