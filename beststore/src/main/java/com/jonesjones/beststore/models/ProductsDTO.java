package com.jonesjones.beststore.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class ProductsDTO {

    @NotEmpty(message = "O nome é necessário!")
    private String name;

    @NotEmpty(message = "A marca é necessária")
    private String brand;

    @NotEmpty(message = "A categoria é necessária!")
    private String category;

    @Min(0)
    private double price;

    @Size(min = 10, message = "A descrição deve conter ao menos 10 caracteres")
    @Size(max = 200, message = "A descrição não pode conter mais de 200 caracteres")
    private String description;

    private MultipartFile imageFile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}
