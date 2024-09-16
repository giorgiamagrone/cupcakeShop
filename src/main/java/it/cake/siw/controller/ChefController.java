package it.cake.siw.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.cake.siw.model.Credentials;
import it.cake.siw.model.Chef;
import it.cake.siw.repository.ChefRepository;
import it.cake.siw.service.CredentialsService;

@Controller
public class ChefController {

    @Autowired
    private ChefRepository chefRepository;
    
    @Autowired
    private CredentialsService credentialsService;

    // Show form to add a new Chef
    @GetMapping("/admin/formNewChef")
    public String formNewChef(Model model) {
        if (chefRepository.count() > 0) {
            model.addAttribute("messaggioErrore", "Esiste già uno chef nel sistema.");
            return "error";  // Show error if chef already exists
        }
        model.addAttribute("chef", new Chef());
        model.addAttribute("credentials", new Credentials()); // Add credentials to the model
        return "admin/formNewChef";
    }

    // Handle new Chef submission
    @PostMapping("/admin/chef")
    public String newChef(@ModelAttribute("chef") Chef chef, 
                          @RequestParam("username") String username, 
                          @RequestParam("password") String password,
                          Model model) {

        // Ensure no other chef exists
        if (chefRepository.count() == 0) {
            try {
                // Save the chef in the database
                chefRepository.save(chef);

                // Create credentials for the chef
                Credentials credentials = new Credentials();
                credentials.setUsername(username);
                credentials.setPassword(password);  // Ensure password encryption in service
                credentials.setChef(chef);

                // Save credentials and assign "CHEF_ROLE"
                credentialsService.saveCredentials(credentials, Credentials.CHEF_ROLE);

                model.addAttribute("successMessage", "Chef aggiunto con successo!");
                return "redirect:/admin/chefDetails";  // Redirect to chef details page

            } catch (Exception e) {
                model.addAttribute("errorMessage", "Errore nell'aggiunta dello chef.");
                return "admin/formNewChef";
            }
        } else {
            model.addAttribute("messaggioErrore", "Esiste già uno chef nel sistema.");
            return "admin/formNewChef";
        }
    }

    // Show details of the Chef
    @GetMapping("/admin/chefDetails")
    public String showChefDetails(Model model) {
        Optional<Chef> chef = chefRepository.findFirstByOrderById();
        if (chef.isPresent()) {
            model.addAttribute("chef", chef.get());
            return "admin/chefDetails";  // Page to display chef details
        } else {
            model.addAttribute("messaggioErrore", "Nessuno chef presente.");
            return "error";
        }
    }

    // Show form to edit the Chef
    @GetMapping("/admin/formEditChef")
    public String formEditChef(Model model) {
        Optional<Chef> chef = chefRepository.findFirstByOrderById();
        if (chef.isPresent()) {
            model.addAttribute("chef", chef.get());
            return "admin/formEditChef";
        } else {
            model.addAttribute("messaggioErrore", "Nessuno chef presente.");
            return "error";
        }
    }

    // Handle chef update
    @PostMapping("/admin/editChef")
    public String updateChef(@ModelAttribute("chef") Chef chef, Model model) {
        Optional<Chef> existingChef = chefRepository.findFirstByOrderById();
        if (existingChef.isPresent()) {
            Chef currentChef = existingChef.get();
            currentChef.setName(chef.getName());
            currentChef.setSurname(chef.getSurname());
            

            // Save updated chef
            chefRepository.save(currentChef);
            model.addAttribute("messaggioSuccesso", "Chef aggiornato con successo");
            return "redirect:/admin/chefDetails";
        } else {
            model.addAttribute("messaggioErrore", "Errore nell'aggiornamento dello chef.");
            return "admin/formEditChef";
        }
    }

    // Delete the Chef
    @GetMapping("/admin/deleteChef")
    public String deleteChef(Model model) {
        Optional<Chef> chef = chefRepository.findFirstByOrderById();
        if (chef.isPresent()) {
            Chef currentChef = chef.get();
            
            // Remove chef's credentials
            credentialsService.deleteCredentialsByChef(currentChef);
            
            // Delete chef
            chefRepository.delete(currentChef);
            model.addAttribute("successMessage", "Chef eliminato con successo.");
            return "redirect:/admin/formNewChef";  // Redirect to form to add a new chef
        } else {
            model.addAttribute("errorMessage", "Nessuno chef presente da eliminare.");
            return "error";
        }
    }
}
