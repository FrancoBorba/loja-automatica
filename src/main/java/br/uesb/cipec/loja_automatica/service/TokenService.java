package br.uesb.cipec.loja_automatica.service;

import org.springframework.beans.factory.annotation.Autowired;

import br.uesb.cipec.loja_automatica.DTO.TokenDTO;
import br.uesb.cipec.loja_automatica.exception.RequiredObjectIsNullException;
import br.uesb.cipec.loja_automatica.exception.ResourceNotFoundException;
import br.uesb.cipec.loja_automatica.mapper.TokenMapper;
import br.uesb.cipec.loja_automatica.repository.TokenRepository;

public class TokenService {
    
    @Autowired
    TokenRepository repository;
    
    @Autowired
    TokenMapper mapper;

    public TokenDTO create(TokenDTO token){
        if(token == null){
            throw new RequiredObjectIsNullException("Token cannot be null.");
        }
        var entity = mapper.toEntity(token);

        repository.save(entity);
  
        var dto = mapper.toDTO(entity);
  
        return dto;

    }

    public TokenDTO findByToken(String token){
        var entity = repository.findByToken(token)
        .orElseThrow(() -> new ResourceNotFoundException("Token not found"));

        var dto = mapper.toDTO(entity);

        return dto;
    }

}
