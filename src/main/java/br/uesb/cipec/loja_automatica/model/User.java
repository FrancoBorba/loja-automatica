package br.uesb.cipec.loja_automatica.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.uesb.cipec.loja_automatica.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity 
@Table(name = "user_account") 
public class User {

    public User() {
    }

    @Id 
    @GeneratedValue(strategy = GenerationType.SEQUENCE) 
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false , unique=true)  //it is not possible two users with the same email address
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false , updatable = false) //it is not possible to change the value 
    private LocalDate dateOfRegistration;

    @Column(nullable = false)
    private boolean isActive;

    private boolean enabled;

    @OneToMany(mappedBy = "user")
    private List<Purchase> purchase = new ArrayList<>();

    @PrePersist //executes automatically before the entity is created
    public void prePersist() {
        this.dateOfRegistration = LocalDate.now();
        this.isActive = true;
        this.enabled = false;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    public UserRole getRole() {
      return role;
    }

    public void setRole(UserRole role) {
      this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration(LocalDate dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Purchase> getPurchase() {
        return purchase;
    }

    public void setPurchase(List<Purchase> purchase) {
        this.purchase = purchase;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((dateOfBirth == null) ? 0 : dateOfBirth.hashCode());
        result = prime * result + ((dateOfRegistration == null) ? 0 : dateOfRegistration.hashCode());
        result = prime * result + (isActive ? 1231 : 1237);
        result = prime * result + (enabled ? 1231 : 1237);
        result = prime * result + ((purchase == null) ? 0 : purchase.hashCode());
        result = prime * result + ((role == null) ? 0 : role.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (dateOfBirth == null) {
            if (other.dateOfBirth != null)
                return false;
        } else if (!dateOfBirth.equals(other.dateOfBirth))
            return false;
        if (dateOfRegistration == null) {
            if (other.dateOfRegistration != null)
                return false;
        } else if (!dateOfRegistration.equals(other.dateOfRegistration))
            return false;
        if (isActive != other.isActive)
            return false;
        if (enabled != other.enabled)
            return false;
        if (purchase == null) {
            if (other.purchase != null)
                return false;
        } else if (!purchase.equals(other.purchase))
            return false;
        if (role != other.role)
            return false;
        return true;
    }

    
}
