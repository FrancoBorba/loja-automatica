package br.uesb.cipec.loja_automatica.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.uesb.cipec.loja_automatica.enums.UserRole;
import br.uesb.cipec.loja_automatica.model.User;

public class UserDetailsImpl implements UserDetails{

  private User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if(user.getRole() == UserRole.ADMIN) {
      return List.of(
        new SimpleGrantedAuthority("ROLE_ADMIN") ,
        new SimpleGrantedAuthority("ROLE_USER"));
    } else{
        return List.of(
        new SimpleGrantedAuthority("ROLE_USER"));
    }
  }

  @Override
  public String getPassword() {
   return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getEmail();
  }

  @Override
  public boolean isEnabled() {
      return user.isActive();
  }

  public User getUser(){
    return user;
  }
  
}
