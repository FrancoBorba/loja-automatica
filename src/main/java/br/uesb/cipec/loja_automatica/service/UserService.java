package br.uesb.cipec.loja_automatica.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.uesb.cipec.loja_automatica.DTO.UserDTO;
import br.uesb.cipec.loja_automatica.exception.RequiredObjectIsNullException;
import br.uesb.cipec.loja_automatica.exception.ResourceNotFoundException;
import br.uesb.cipec.loja_automatica.mapper.UserMapper;
import br.uesb.cipec.loja_automatica.model.User;
import br.uesb.cipec.loja_automatica.repository.UserRepository;

@Service
public class UserService {
 
    @Autowired
    UserRepository repository;

    @Autowired
    UserMapper mapper;

    public UserDTO create(UserDTO user){
        if (user == null) {
            throw new RequiredObjectIsNullException("User cannot be null.");
        }
        if (repository.findByEmail(user.getEmail()).isPresent()){
            throw new IllegalArgumentException("Email already registered");
        }

        var entity = mapper.toEntity(user);
        repository.save(entity); //JPA automatically update the entity with the ID generated and other managed fields

        var dto = mapper.toDTO(entity);
        return dto;
    }

    public UserDTO findById(Long id){
        var entity = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        var dto = mapper.toDTO(entity);

        return dto;
    }

    public List<UserDTO> findAll(){
        var entities = repository.findAll();

        var users = entities.stream()
        .map(mapper::toDTO)
        .toList();

        return users;
    }

    public UserDTO update(UserDTO user){
        if(user == null){
         throw new RequiredObjectIsNullException("User cannot be null.");
        }

        User entity = repository.findById(user.getId()).
        orElseThrow(()-> new ResourceNotFoundException("User not found"));
  
        Optional<User> userWithSameEmail = repository.findByEmail(user.getEmail());
        if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Email address already in use by another user.");
        }
        entity.setName(user.getName());
        entity.setEmail(user.getEmail()); 
        entity.setDateOfBirth(user.getDateOfBirth());
        entity.setActive(user.isActive());
  
        repository.save(entity);
  
        var dto = mapper.toDTO(entity);
        return dto;
    }
  

    public void delete(Long id){
        User entity = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found"));

        repository.delete(entity);
    }
    
}
