package br.uesb.cipec.loja_automatica.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.uesb.cipec.loja_automatica.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);
}
