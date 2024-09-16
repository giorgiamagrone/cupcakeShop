package it.cake.siw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.cake.siw.model.Cupcake;
import it.cake.siw.model.Credentials;
import it.cake.siw.model.Chef;
import it.cake.siw.repository.CupcakeRepository;
import it.cake.siw.service.CredentialsService;

import java.util.List;

@Controller
public class CupcakeController {

    @Autowired
    private CupcakeRepository cupcakeRepository;

    @Autowired
    private CredentialsService credentialsService;
    
    
    @GetMapping("/cupcakes/{id}")
    public String viewCupcakes(@PathVariable("id") Long id, Model model) {
        // Trova il cupcake per ID
        Cupcake cupcake = cupcakeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ID cupcake non valido: " + id));
        
        // Aggiungi il cupcake al modello
        model.addAttribute("cupcake", cupcake);
        
        return "/cupcake"; // Il nome del template HTML per visualizzare il cupcake
    }
    
    @GetMapping("/cupcake/{id}")
    public String viewCupcake(@PathVariable("id") Long id, Model model) {
        // Trova il cupcake per ID
        Cupcake cupcake = cupcakeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ID cupcake non valido: " + id));
        
        // Aggiungi il cupcake al modello
        model.addAttribute("cupcake", cupcake);
        
        return "/cupcake"; // Il nome del template HTML per visualizzare il cupcake
    }

    @GetMapping("/cupcakes")
    public String viewAllCupcakes(Model model) {
        // Recupera tutti i cupcake
        List<Cupcake> cupcakes = (List<Cupcake>) cupcakeRepository.findAll();
        
        // Aggiungi la lista di cupcake al modello
        model.addAttribute("cupcakes", cupcakes);
        
        return "/cupcakes"; // Il nome del template HTML per visualizzare tutti i cupcake
    }


    @GetMapping("/formSearchCupcake")
    public String formSearchCupcake(Model model) {
        model.addAttribute("searchCupcake", new Cupcake());
        return "formSearchCupcake";
    }

    @PostMapping("/formSearchCupcake")
    public String searchCupcake(Model model, @RequestParam("name") String name) {
        model.addAttribute("cupcakes", this.cupcakeRepository.findByName(name));
        return "foundCupcake";
    }

    @GetMapping("/chef/indexCupcake/{id}")
    public String getCupcake(@PathVariable("id") Long id, Model model) {
        Cupcake cupcake = cupcakeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("ID cupcake non valido: " + id));

        model.addAttribute("cupcake", cupcake);
        return "chef/indexCupcake";  // Pagina per visualizzare i dettagli del cupcake
    }

    @GetMapping("/chef/indexCupcakes")
    public String indexCupcakes(Model model) {
        // Ottiene tutti i cupcake
        List<Cupcake> cupcakes = (List<Cupcake>) cupcakeRepository.findAll();
        model.addAttribute("cupcakes", cupcakes);
        return "chef/indexCupcakes";
    }

    @GetMapping("/chef/formNewCupcake")
    public String formNewCupcake(Model model) {
        model.addAttribute("cupcake", new Cupcake());
        return "chef/formNewCupcake";
    }

    @PostMapping("/chef/indexCupcake")
    public String newCupcake(@ModelAttribute Cupcake cupcake, BindingResult result, Model model) {
        // Salva il cupcake
        cupcakeRepository.save(cupcake);

        model.addAttribute("successMessage", "Cupcake aggiunto con successo.");
        return "redirect:/chef/indexCupcake/" + cupcake.getId();  // Redirect alla pagina di successo o al dettaglio del cupcake
    }

    @GetMapping("/chef/selectCupcakeToEdit")
    public String selectCupcakeToEdit(Model model) {
        // Recupera tutti i cupcake
        List<Cupcake> cupcakes = (List<Cupcake>) cupcakeRepository.findAll();
        model.addAttribute("cupcakes", cupcakes);
        return "chef/selectCupcakeToEdit";
    }

    @GetMapping("/chef/formEditCupcake/{id}")
    public String showEditCupcakeForm(@PathVariable("id") Long id, Model model) {
        Cupcake cupcake = cupcakeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("ID cupcake non valido: " + id));

        model.addAttribute("cupcake", cupcake);
        return "chef/formEditCupcake";
    }

    @PostMapping("/chef/formEditCupcake/{id}")
    public String formEditCupcake(@PathVariable("id") Long cupcakeId,
                                  @ModelAttribute("cupcake") Cupcake cupcake,
                                  BindingResult result, Model model) {

        // Trova il cupcake esistente
        Cupcake existingCupcake = cupcakeRepository.findByIdWithDetails(cupcakeId).orElse(null);

        if (existingCupcake != null) {
            // Aggiorna i campi del cupcake
            existingCupcake.setName(cupcake.getName());
            existingCupcake.setPrice(cupcake.getPrice());

            // Salva il cupcake aggiornato
            cupcakeRepository.save(existingCupcake);
        }

        return "redirect:/chef/selectCupcakeToEdit";  // Redirect dopo il salvataggio
    }

    @GetMapping("/chef/selectCupcakeToDelete")
    public String selectCupcakeToDelete(Model model) {
        // Recupera tutti i cupcake
        List<Cupcake> cupcakes = (List<Cupcake>) cupcakeRepository.findAll();

        if (cupcakes.isEmpty()) {
            model.addAttribute("message", "Nessun cupcake disponibile per l'eliminazione.");
        }

        model.addAttribute("cupcakes", cupcakes);
        return "chef/selectCupcakeToDelete";
    }


    @PostMapping("/chef/deleteCupcake/{id}")
    public String deleteCupcake(@PathVariable("id") Long id, Model model) {
        Cupcake cupcake = cupcakeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("ID cupcake non valido: " + id));

        // Elimina il cupcake
        cupcakeRepository.deleteById(id);

        return "redirect:/chef/selectCupcakeToDelete";  // Reindirizza alla pagina per selezionare altri cupcake da eliminare
    }
}
