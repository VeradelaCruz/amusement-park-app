package com.example.games_service.validation;


import com.example.games_service.models.Game;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// Implementa la interfaz ConstraintValidator, que recibe dos tipos:
// 1. La anotación que definimos (@ValidTimeRange)
// 2. La clase que vamos a validar (Game)
public class TimeRangeValidator implements ConstraintValidator<ValidTimeRange, Game> {

    // Este método se llama una vez al inicializar el validador.
    // Lo usamos si necesitamos parámetros de la anotación. Aquí no usamos ninguno, pero debe estar presente.
    @Override
    public void initialize(ValidTimeRange constraintAnnotation) {
        // No se necesita inicialización en este caso
    }

    // Método principal que valida la clase
    @Override
    public boolean isValid(Game game, ConstraintValidatorContext context) {
        if (game == null) {
            // Si el objeto es null, lo dejamos pasar. Otra opción sería devolver false.
            return true;
        }

        // Obtenemos las horas de inicio y fin
        if (game.getStartTime() == null || game.getEndTime() == null) {
            // Si alguna hora es null, no podemos validar, consideramos válido
            return true;
        }

        boolean valid = game.getEndTime().isAfter(game.getStartTime());

        if (!valid) {
            // Si no es válido, configuramos un mensaje dinámico que incluya los valores reales
            context.disableDefaultConstraintViolation(); // Desactiva el mensaje por defecto
            context.buildConstraintViolationWithTemplate(
                    "End time (" + game.getEndTime() + ") must be after start time (" + game.getStartTime() + ")"
            ).addConstraintViolation(); // Agrega nuestro mensaje personalizado
        }

        return valid; // Devuelve true si la hora de fin es posterior a la de inicio
    }
}
