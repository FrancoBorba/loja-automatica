package br.uesb.cipec.loja_automatica.mapper;

import org.mapstruct.Mapper;

import br.uesb.cipec.loja_automatica.DTO.TokenDTO;
import br.uesb.cipec.loja_automatica.model.Token;

@Mapper(componentModel = "spring")
public interface TokenMapper {
    public TokenDTO toDTO(Token token);

    public Token toEntity(TokenDTO token);
}
