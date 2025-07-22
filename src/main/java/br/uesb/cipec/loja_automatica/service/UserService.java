package br.uesb.cipec.loja_automatica.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.uesb.cipec.loja_automatica.model.User;
import br.uesb.cipec.loja_automatica.repository.UserRepository;

@Service
public class UserService {
 
    private UserRepository repository;

    public UserService(UserRepository repository){
        this.repository = repository;
    }

    public User create(User user){
        return repository.save(user);
    }

    public Optional<User> findById(Long id){
        return repository.findById(id);
    }

    public List<User> findAll(){
        return repository.findAll();
    }

    public void delete(Long id){
        repository.deleteById(id);
    }
    
}
