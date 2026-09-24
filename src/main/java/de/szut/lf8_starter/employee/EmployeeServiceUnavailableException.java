package de.szut.lf8_starter.employee;

import org.springframework.http.HttpStatusCode;

public class EmployeeServiceUnavailableException extends RuntimeException {

    public EmployeeServiceUnavailableException(HttpStatusCode status, Throwable cause) {
        super("Employee-Service antwortet mit HTTP " + status.value() + " – Anfrage fehlgeschlagen", cause);
    }

    public EmployeeServiceUnavailableException(Throwable cause) {
        super("Employee-Service nicht erreichbar – läuft docker compose?", cause);
    }
}
