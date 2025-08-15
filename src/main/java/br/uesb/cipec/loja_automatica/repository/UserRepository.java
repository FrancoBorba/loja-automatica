package br.uesb.cipec.loja_automatica.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.uesb.cipec.loja_automatica.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);
   
    boolean existsByEmailAndEnabledTrue(String email);
   
    @Query("SELECT u.enabled FROM User u WHERE u.id = :id")
    Optional<Boolean> isUserEnabled(@Param("id") Long id);
}
