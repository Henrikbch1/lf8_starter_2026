package de.szut.lf8_starter.hello;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HelloCreateDto(@NotBlank(message = "darf nicht leer sein") @Size(min = 3, message = "muss mindestens 3 Zeichen lang sein") String message) {
}
