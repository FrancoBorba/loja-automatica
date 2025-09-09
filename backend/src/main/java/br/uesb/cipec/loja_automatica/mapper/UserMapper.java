package br.uesb.cipec.loja_automatica.mapper;

import org.mapstruct.Mapper;

import br.uesb.cipec.loja_automatica.DTO.UserDTO;
import br.uesb.cipec.loja_automatica.DTO.UserRegisterDTO;
import br.uesb.cipec.loja_automatica.DTO.UserResponseDTO;
import br.uesb.cipec.loja_automatica.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    public UserDTO toDTO(User user);

    public UserResponseDTO toResponseDTO(User user);

    public User toEntity(UserDTO user);

    public User toEntity(UserRegisterDTO user);
}
