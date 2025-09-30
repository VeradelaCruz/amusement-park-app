package com.example.employees_service.models;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "employees")
public class Employee {
    @Id
    private String id;
    @NotEmpty(message = "Name can not be empty")
    private String firstName;
    @NotEmpty(message = "Last name can not be empty")
    private String lastName;
    @NotEmpty(message = "User name can not be empty")
    private String username;
    @NotBlank(message = "Password cannot be blank") // No puede estar vacío
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters") // Longitud mínima y máxima
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one number"
    ) // Reglas de complejidad
    private String password;
    @Indexed(unique = true)
    private String email;
    @Size(max= 8, message = "Phone number can not be longer than 8 characters")
    private String phone;

    @NotNull(message = "This employee must be assigned to a game")
    private String gameId;

}
