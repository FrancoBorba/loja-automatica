package br.uesb.cipec.loja_automatica.DTO;

import java.time.LocalDateTime;

import org.springframework.hateoas.RepresentationModel;

import jakarta.validation.constraints.NotBlank;

public class TokenDTO extends RepresentationModel<TokenDTO> {

    public TokenDTO() {
    }

    public TokenDTO(@NotBlank(message = "Token is required") String token, LocalDateTime createdAt, LocalDateTime expiresAt,
            long userID) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.userID = userID;
    }

    private Long id;

    @NotBlank(message = "Token is required")
    private String token;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private LocalDateTime confirmedAt;

    private long userID;

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


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }


    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }


    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }


    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }


    public long getUserID() {
        return userID;
    }


    public void setUserID(long userID) {
        this.userID = userID;
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
        result = prime * result + (int) (userID ^ (userID >>> 32));
        return result;
    }
}