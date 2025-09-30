package com.example.buyer_service.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyerDTO {

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @Email
    @Indexed(unique = true)
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Phone number must be valid") // opcional
    private String phone;

    @Size(max = 20, message = "Document ID must be at most 20 characters")
    private String documentId;
}
