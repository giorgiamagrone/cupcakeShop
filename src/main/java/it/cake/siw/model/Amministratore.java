package it.cake.siw.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Amministratore {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;  // Identificatore unico per l'amministratore

    // Eventuali altri campi rilevanti per l'amministratore
    private String username;

    // Getters e Setters
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
}
