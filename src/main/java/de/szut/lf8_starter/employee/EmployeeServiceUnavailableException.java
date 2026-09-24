package de.szut.lf8_starter.employee;

public class EmployeeServiceUnavailableException extends RuntimeException {

    public EmployeeServiceUnavailableException(Throwable cause) {
        super("Employee-Service nicht erreichbar – läuft docker compose?", cause);
    }
}
