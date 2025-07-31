package br.uesb.cipec.loja_automatica.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.uesb.cipec.loja_automatica.DTO.TokenDTO;
import br.uesb.cipec.loja_automatica.model.Token;

@Mapper(componentModel = "spring")
public interface TokenMapper {
    @Mapping(source = "user.id", target = "userID")
    public TokenDTO toDTO(Token token);

    @Mapping(source = "userID", target = "user.id")
    public Token toEntity(TokenDTO token);
}
