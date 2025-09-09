package br.uesb.cipec.loja_automatica.mapper;

import br.uesb.cipec.loja_automatica.DTO.ProductDTO;
import br.uesb.cipec.loja_automatica.document.ProductDocument;
import br.uesb.cipec.loja_automatica.model.Product;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ProductDocumentMapper {

    ProductDocument toDocument(Product product);

    ProductDTO toDTO(ProductDocument document);
}