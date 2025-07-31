package br.uesb.cipec.loja_automatica.DTO;

import java.time.LocalDate;

import org.springframework.hateoas.RepresentationModel;

import br.uesb.cipec.loja_automatica.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;

public class TokenDTO extends RepresentationModel<TokenDTO> {

    public TokenDTO() {
    }

    private Long id;

    @NotBlank(message = "Token is required")
    private String token;

    private LocalDate createdAt;

    private LocalDate expiresAt;

    private LocalDate confirmedAt;

    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDate expiresAt) {
        this.expiresAt = expiresAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((token == null) ? 0 : token.hashCode());
        result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
        result = prime * result + ((expiresAt == null) ? 0 : expiresAt.hashCode());
        result = prime * result + ((confirmedAt == null) ? 0 : confirmedAt.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }
    
}
