package br.uesb.cipec.loja_automatica.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.uesb.cipec.loja_automatica.model.Token;

public interface TokenRepository extends JpaRepository<Token, Long>{
    Optional<Token> findByToken(String token);
}
