package it.cake.siw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class UploadController {

    // Change upload directory to the static folder for serving images
    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static/images";

    // Display the form to upload an image
    @GetMapping("/uploadimage")
    public String displayUploadForm() {
        return "imageupload/index";  // Replace this with your form view for uploading images
    }

    // Handle image upload
    @PostMapping("/upload")
    public String uploadImage(Model model, @RequestParam("image") MultipartFile file) throws IOException {
        // Ensure that the file isn't empty before proceeding
        if (!file.isEmpty()) {
            StringBuilder fileNames = new StringBuilder();
            // Define the path where the image will be saved
            Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
            fileNames.append(file.getOriginalFilename());

            // Save the file to the specified path
            Files.write(fileNameAndPath, file.getBytes());

            // Store the relative URL of the uploaded image for use in the view (e.g., storing in the database)
            String imageUrl = "/images/" + file.getOriginalFilename();
            model.addAttribute("imageUrl", imageUrl);  // Use this URL in your cupcake views
            
            // Optionally, you can save this URL in the cupcake entity (e.g., update the corresponding cupcake)

            // Success message
            model.addAttribute("msg", "Uploaded image: " + fileNames.toString());
        } else {
            model.addAttribute("msg", "Please select a file to upload.");
        }

        return "imageupload/index";  // Return the upload form view, or redirect as needed
    }
}
