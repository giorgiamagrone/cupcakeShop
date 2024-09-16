package it.cake.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "credentials") 
public class Credentials {

    public static final String ADMIN_ROLE = "ADMIN";
    public static final String CHEF_ROLE = "CHEF";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String role;

    // Aggiungi la relazione con il presidente
    @OneToOne
    @JoinColumn(name = "chef_id")
    private Chef chef;  

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Getter e Setter per il Presidente
    public Chef getChef() {
        return chef;
    }

    public void setChef(Chef president) {
        this.chef = president;
    }

    // Helper methods
    public boolean isAdmin() {
        return ADMIN_ROLE.equals(this.role);
    }

    public boolean isPresident() {
        return CHEF_ROLE.equals(this.role);
    }
}
