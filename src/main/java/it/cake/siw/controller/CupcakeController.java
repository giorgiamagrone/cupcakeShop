package it.cake.siw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.cake.siw.model.Cupcake;
import it.cake.siw.repository.CupcakeRepository;
import it.cake.siw.service.CredentialsService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class CupcakeController {

    @Autowired
    private CupcakeRepository cupcakeRepository;

    @Autowired
    private CredentialsService credentialsService;

    @GetMapping("/cupcakes/{id}")
    public String viewCupcake(@PathVariable("id") Long id, Model model) {
        // Find the cupcake by ID
        Cupcake cupcake = cupcakeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid cupcake ID: " + id));
        
        // Add cupcake to the model
        model.addAttribute("cupcake", cupcake);
        
        return "/cupcake";  // Return the Thymeleaf template for cupcake details
    }

    @GetMapping("/cupcakes")
    public String viewAllCupcakes(Model model) {
        // Get all cupcakes
        List<Cupcake> cupcakes = (List<Cupcake>) cupcakeRepository.findAll();
        
        // Add the list of cupcakes to the model
        model.addAttribute("cupcakes", cupcakes);
        
        return "/cupcakes";  // Return the Thymeleaf template for all cupcakes
    }

    @GetMapping("/chef/indexCupcake/{id}")
    public String getCupcake(@PathVariable("id") Long id, Model model) {
        Cupcake cupcake = cupcakeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid cupcake ID: " + id));

        model.addAttribute("cupcake", cupcake);
        return "chef/indexCupcake";  // Return the Thymeleaf template for the chef's cupcake details
    }

    @GetMapping("/chef/indexCupcakes")
    public String indexCupcakes(Model model) {
        List<Cupcake> cupcakes = (List<Cupcake>) cupcakeRepository.findAll();
        model.addAttribute("cupcakes", cupcakes);
        return "chef/indexCupcakes";  // Return the Thymeleaf template for the list of chef's cupcakes
    }

    @GetMapping("/chef/formNewCupcake")
    public String formNewCupcake(Model model) {
        model.addAttribute("cupcake", new Cupcake());
        return "chef/formNewCupcake";  // Return the Thymeleaf template for the new cupcake form
    }

    @PostMapping("/chef/indexCupcake")
    public String newCupcake(@ModelAttribute Cupcake cupcake, 
                             @RequestParam("image") MultipartFile imageFile, 
                             BindingResult result, 
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Errore nell'inserimento del cupcake.");
            return "chef/indexCupcake";  // Return to form on error
        }

        try {
            // Check if the image file is not empty
            if (!imageFile.isEmpty()) {
                // Define the path to save the image in the external directory
                String staticImagesDir = System.getProperty("user.dir") + "/uploads/images/";
                File uploadFolder = new File(staticImagesDir);

                if (!uploadFolder.exists()) {
                    uploadFolder.mkdirs();  // Create directory if it doesn't exist
                }

                String fileName = imageFile.getOriginalFilename();
                File destinationFile = new File(staticImagesDir, fileName);

                // Save the image file to the disk
                imageFile.transferTo(destinationFile);

                // Set the image URL in the Cupcake object for database saving
                cupcake.setImageUrl("/uploads/images/" + fileName);
            }

            // Save the cupcake in the database
            cupcakeRepository.save(cupcake);
            model.addAttribute("successMessage", "Cupcake aggiunto con successo.");

        } catch (IOException e) {
            model.addAttribute("errorMessage", "Errore nel caricamento dell'immagine.");
            e.printStackTrace();
            return "chef/indexCupcake";  // Return to form on error
        }

        return "redirect:/chef/indexCupcake/" + cupcake.getId();  // Redirect to cupcake details
    }

    @GetMapping("/chef/selectCupcakeToEdit")
    public String selectCupcakeToEdit(Model model) {
        List<Cupcake> cupcakes = (List<Cupcake>) cupcakeRepository.findAll();
        model.addAttribute("cupcakes", cupcakes);
        return "chef/selectCupcakeToEdit";  // Return the Thymeleaf template for selecting a cupcake to edit
    }

    @GetMapping("/chef/formEditCupcake/{id}")
    public String showEditCupcakeForm(@PathVariable("id") Long id, Model model) {
        Cupcake cupcake = cupcakeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid cupcake ID: " + id));
        model.addAttribute("cupcake", cupcake);
        return "chef/formEditCupcake";  // Return the Thymeleaf template for editing the cupcake
    }

    @PostMapping("/chef/formEditCupcake/{id}")
    public String formEditCupcake(@PathVariable("id") Long cupcakeId,
                                  @ModelAttribute("cupcake") Cupcake cupcake,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "chef/formEditCupcake";  // Return to form on error
        }

        // Find the existing cupcake by ID
        Cupcake existingCupcake = cupcakeRepository.findById(cupcakeId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid cupcake ID: " + cupcakeId));

        // Update the cupcake's name and price
        existingCupcake.setName(cupcake.getName());
        existingCupcake.setPrice(cupcake.getPrice());

        // Save the updated cupcake
        cupcakeRepository.save(existingCupcake);

        return "redirect:/chef/selectCupcakeToEdit";  // Redirect after saving
    }

    @GetMapping("/chef/selectCupcakeToDelete")
    public String selectCupcakeToDelete(Model model) {
        List<Cupcake> cupcakes = (List<Cupcake>) cupcakeRepository.findAll();
        if (cupcakes.isEmpty()) {
            model.addAttribute("message", "Nessun cupcake disponibile per l'eliminazione.");
        }
        model.addAttribute("cupcakes", cupcakes);
        return "chef/selectCupcakeToDelete";  // Return the Thymeleaf template for selecting a cupcake to delete
    }

    @PostMapping("/chef/deleteCupcake/{id}")
    public String deleteCupcake(@PathVariable("id") Long id, Model model) {
        Cupcake cupcake = cupcakeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid cupcake ID: " + id));

        // Delete the cupcake
        cupcakeRepository.deleteById(id);

        return "redirect:/chef/selectCupcakeToDelete";  // Redirect after deletion
    }
}
