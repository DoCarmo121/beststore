package com.jonesjones.beststore.controllers;

import com.jonesjones.beststore.models.Product;
import com.jonesjones.beststore.models.ProductsDTO;
import com.jonesjones.beststore.services.ProductsRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

//retorna views (Ex: paginas html). Já o RestController retorna dados serializados (formato json)
@Controller
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductsRepository productsRepository;

    @GetMapping("")
    public String showProductsList(Model model){
        List<Product>products=productsRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("products", products);
        return "products/index";
        //Model é uma interface para definir um holder de atributos. Nesse caso, o holder "products" armazena a lista products
        //que é chamada na página html.
    }

    @GetMapping("/create")
    public String showCreatePage(Model model){
        ProductsDTO productsDTO=new ProductsDTO();
        model.addAttribute("productsDTO", productsDTO);
        return "products/CreateProduct";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute ProductsDTO productsDTO, BindingResult result){
        if(productsDTO.getImageFile().isEmpty()){
            result.addError(new FieldError("productsDTO", "imageFile", "The image file is required."));
        }
        if(result.hasErrors()){
            return "products/CreateProduct";
        }
        //save image file
        MultipartFile image = productsDTO.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }

            try(InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e){
            System.out.println("Exception: " + e.getMessage());
        }

        Product product = new Product();
        product.setName(productsDTO.getName());
        product.setBrand(productsDTO.getBrand());
        product.setCategory(productsDTO.getCategory());
        product.setPrice(productsDTO.getPrice());
        product.setDescription(productsDTO.getDescription());
        product.setCreatedAt(createdAt);
        product.setImageFileName(storageFileName);

        productsRepository.save(product);

        return "redirect:/products";
    }

    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam int id){

        try {
            Product product = productsRepository.findById(id).get();
            model.addAttribute("product", product);

            ProductsDTO productsDTO = new ProductsDTO();
            productsDTO.setName(product.getName());
            productsDTO.setBrand(product.getBrand());
            productsDTO.setCategory(product.getCategory());
            productsDTO.setPrice(product.getPrice());
            productsDTO.setDescription(product.getDescription());

            model.addAttribute("productsDTO", productsDTO);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return "redirect:/products";
        }

        return "products/EditProduct";
    }

    @PostMapping("/edit")
    public String updateProduct(Model model, @RequestParam int id, @Valid @ModelAttribute ProductsDTO productsDTO, BindingResult result){

        try {
            Product product = productsRepository.findById(id).get();
            model.addAttribute("product", product);

            if(result.hasErrors()){
                return "products/EditProduct";
            }

            if(!productsDTO.getImageFile().isEmpty()){
                //delete old image
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());

                try {
                    Files.delete(oldImagePath);
                }catch (Exception e){
                    System.out.println("Exception: " + e.getMessage());
                }

                //save new image file
                MultipartFile image = productsDTO.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

                try(InputStream inputStream = image.getInputStream()) {
                   Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                           StandardCopyOption.REPLACE_EXISTING);
                }

                product.setImageFileName(storageFileName);
            }

            product.setName(productsDTO.getName());
            product.setBrand(productsDTO.getBrand());
            product.setCategory(productsDTO.getCategory());
            product.setPrice(productsDTO.getPrice());
            product.setDescription(productsDTO.getDescription());

            productsRepository.save(product);

        }catch (Exception e){
            System.out.println("Exception: " + e.getMessage());
        }

        return "redirect:/products";
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id){

        try {
            Product product = productsRepository.findById(id).get();

            //delete product image
            Path imagePath = Paths.get("public/images/" + product.getImageFileName());
            try {
                Files.delete(imagePath);
            }catch (Exception e){
                System.out.println("Exception: " + e.getMessage());
            }

            //delete the product
            productsRepository.delete(product);

        }catch (Exception e){
            System.out.println("Exception: " + e.getMessage());
        }

        return "redirect:/products";
    }


}
