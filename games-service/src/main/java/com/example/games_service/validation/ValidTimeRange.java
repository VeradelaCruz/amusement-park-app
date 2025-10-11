package com.example.games_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

// Indica que esta anotación será incluida en la documentación JavaDoc
@Documented

// Marca esta anotación como una restricción de validación y le dice a Spring/Hibernate
// qué clase se encargará de implementar la lógica de validación
@Constraint(validatedBy = TimeRangeValidator.class)

// Define los lugares donde se puede aplicar la anotación.
// En este caso, @Target(TYPE) indica que se aplica a clases completas (no a campos individuales)
@Target({ ElementType.TYPE })

// Define la política de retención de la anotación.
// RUNTIME significa que la anotación estará disponible en tiempo de ejecución,
// lo que permite que el validador la lea cuando se ejecute la aplicación
@Retention(RetentionPolicy.RUNTIME)

// Declaración de la anotación personalizada
public @interface ValidTimeRange {

    // Mensaje de error por defecto si la validación falla
    String message() default "End time must be after start time";

    // Permite agrupar restricciones. Por defecto, se deja vacío
    Class<?>[] groups() default {};

    // Permite asociar metadatos adicionales a la restricción. Por defecto, se deja vacío
    Class<? extends Payload>[] payload() default {};
}

