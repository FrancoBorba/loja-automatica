package br.uesb.cipec.loja_automatica.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.uesb.cipec.loja_automatica.DTO.TokenDTO;
import br.uesb.cipec.loja_automatica.exception.RequiredObjectIsNullException;
import br.uesb.cipec.loja_automatica.exception.ResourceNotFoundException;
import br.uesb.cipec.loja_automatica.mapper.TokenMapper;
import br.uesb.cipec.loja_automatica.model.Token;
import br.uesb.cipec.loja_automatica.repository.TokenRepository;

@Service
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

    public void confirmToken(String token){
        //check if the token exists        
        Token confirmedToken = repository.findByToken(token)
            .orElseThrow(() -> new ResourceNotFoundException("Token not found"));

        //check if the user is already verified

        //check if the token has expired
        LocalDateTime expiresAt = confirmedToken.getExpiresAt();
        if(expiresAt.isBefore(LocalDateTime.now())){
            throw new IllegalStateException("Token expired");
        }

        //if everything is ok update the confirmation time
        confirmedToken.setConfirmedAt(LocalDateTime.now());
        repository.save(confirmedToken);
        
        //enable the user
    }

}
