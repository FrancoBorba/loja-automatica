package br.uesb.cipec.loja_automatica.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.uesb.cipec.loja_automatica.DTO.TokenDTO;
import br.uesb.cipec.loja_automatica.exception.ActiveTokenExistsException;
import br.uesb.cipec.loja_automatica.exception.RequiredObjectIsNullException;
import br.uesb.cipec.loja_automatica.exception.ResourceNotFoundException;
import br.uesb.cipec.loja_automatica.exception.TokenAlreadyConfirmedException;
import br.uesb.cipec.loja_automatica.exception.TokenExpiredException;
import br.uesb.cipec.loja_automatica.mapper.TokenMapper;
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
        
        //verifies if the already exists an active token for user
        var existingToken = repository.findByUserIdAndConfirmedAtIsNullAndExpiresAtAfter(
            token.getUserID(), 
            LocalDateTime.now()
        );
        if (existingToken.isPresent()) {
            throw new ActiveTokenExistsException(
                "An active confirmation token already exists for this user. Please check your email or wait for it to expire."
            );
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
              
        TokenDTO confirmedToken = findByToken(token);
        
        //check if the token has been confirmed
        if(confirmedToken.getConfirmedAt() != null){
            throw new TokenAlreadyConfirmedException("Token already confirmed.");
        }
        

        //check if the token has expired
        LocalDateTime expiresAt = confirmedToken.getExpiresAt();
        if(expiresAt.isBefore(LocalDateTime.now())){
            throw new TokenExpiredException("Token expired. Please request a new confirmation link.");
        }

        //if everything is ok update the confirmation time
        confirmedToken.setConfirmedAt(LocalDateTime.now());
        var entity = mapper.toEntity(confirmedToken);
        repository.save(entity);
    
    }

}
