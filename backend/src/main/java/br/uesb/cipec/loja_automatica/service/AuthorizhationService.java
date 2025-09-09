package br.uesb.cipec.loja_automatica.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.uesb.cipec.loja_automatica.model.User;
import br.uesb.cipec.loja_automatica.repository.UserRepository;
import br.uesb.cipec.loja_automatica.security.UserDetailsImpl;

@Service
public class AuthorizhationService implements UserDetailsService {

  @Autowired
  UserRepository repository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
      //  Searches for the user (the User entity) in the repository.
    User user = repository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email));

    // "Adapts" the User entity to the UserDetails format that Spring Security understands
    return new UserDetailsImpl(user);
  }

  
}