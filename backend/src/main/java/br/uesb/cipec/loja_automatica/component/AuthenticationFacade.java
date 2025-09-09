package br.uesb.cipec.loja_automatica.component;// ou outro pacote de sua escolha

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import br.uesb.cipec.loja_automatica.model.User;
import br.uesb.cipec.loja_automatica.security.UserDetailsImpl;

@Component
public class AuthenticationFacade {

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null; 
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getUser();
    }
}