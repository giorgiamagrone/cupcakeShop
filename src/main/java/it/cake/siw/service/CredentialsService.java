package it.cake.siw.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.cake.siw.model.Chef;
import it.cake.siw.model.Credentials;
import it.cake.siw.repository.CredentialsRepository;

@Service
public class CredentialsService {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected CredentialsRepository credentialsRepository;

    // Recupera le credenziali tramite ID
    @Transactional
    public Credentials getCredentials(Long id) {
        Optional<Credentials> result = this.credentialsRepository.findById(id);
        return result.orElse(null);
    }

    @Transactional
    public Credentials getCredentials(String username) {
        Optional<Credentials> result = this.credentialsRepository.findByUsername(username);
        if(result.isPresent()) {
            System.out.println("User found: " + result.get().getUsername());
        } else {
            System.out.println("User not found: " + username);
        }
        return result.orElse(null);
    }

    
    @Transactional
    public Credentials saveCredentials(Credentials credentials, String role) {
        credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
        credentials.setRole(role);  // Assegna un ruolo all'utente
        return this.credentialsRepository.save(credentials);
    }
    public boolean isAdmin(Credentials credentials) {
        return "ADMIN".equals(credentials.getRole());
    }

    public boolean isPresident(Credentials credentials) {
        return "PRESIDENT".equals(credentials.getRole());
    }

	public void deleteCredentialsByChef(Chef currentChef) {
		// TODO Auto-generated method stub
		
	}


}
