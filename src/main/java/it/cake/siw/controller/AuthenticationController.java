package it.cake.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.cake.siw.model.Credentials;
import it.cake.siw.service.CredentialsService;

@Controller
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CredentialsService credentialsService;

    // Pagina di login
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Gestione del login fallito
    @GetMapping("/failure")
    public String failure(Model model) {
        model.addAttribute("errorLogin", "Username o password errati!");
        return "login";
    }


 // Gestione del successo post-login
    @GetMapping("/success")
    public String loginSuccess(Model model) {
        // Recupera l'utente autenticato dal SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Recupera le credenziali dal database usando lo username dell'utente autenticato
        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());

        // Verifica il ruolo e reindirizza alla pagina corretta
        if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
            model.addAttribute("admin", credentials);
            return "redirect:/admin/indexAdmin";  // Reindirizza alla dashboard admin
        } else if (credentials.getRole().equals(Credentials.CHEF_ROLE)) {
            model.addAttribute("chef", credentials);
            return "redirect:/chef/indexChef";  // Reindirizza alla dashboard del presidente
        } else {
            return "redirect:/login?error=true";  // In caso di errore, reindirizza al login con errore
        }
    }
    // Index Admin: Gestito dall'amministratore
    @GetMapping("/admin/indexAdmin")
    public String indexAdmin(Model model) {
        // Recupera l'utente autenticato
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Aggiungi i dettagli dell'admin al modello
        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
        model.addAttribute("admin", credentials);

        // Ritorna alla vista dell'index per l'admin
        return "admin/indexAdmin";  // Nome del template HTML per la pagina dell'admin
    }

 // Index President: Gestito dal presidente
    @GetMapping("/chef/indexChef")
    public String indexPresident(Model model) {
        // Recupera l'utente autenticato
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Aggiungi i dettagli del presidente al modello
        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
        model.addAttribute("chef", credentials);

        // Ritorna alla vista dell'index per il presidente
        return "chef/indexChef";  // Nome del template HTML per la pagina del presidente
    }
    
    
    // Logout
    @GetMapping("/logout")
    public String logout() {
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }
}
