package de.szut.lf8_starter.hello;

import jakarta.validation.constraints.Size;

public record HelloCreateDto(@Size(min = 3) String message) {
}
