package br.uesb.cipec.loja_automatica.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.uesb.cipec.loja_automatica.DTO.UserDTO;
import br.uesb.cipec.loja_automatica.mapper.UserMapper;
import br.uesb.cipec.loja_automatica.model.User;
import br.uesb.cipec.loja_automatica.repository.UserRepository;

@Service
public class UserConfirmationService {
    
    @Autowired
    UserMapper mapper;

    @Autowired
    UserRepository repository;

    public void enableUser(Long userID){
        var user = repository.findById(userID);
        User entity = user.get();
        entity.setEnabled(true);
        repository.save(entity);
    }
}
