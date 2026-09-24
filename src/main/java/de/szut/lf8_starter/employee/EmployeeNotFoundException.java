package de.szut.lf8_starter.employee;
public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(long id) { super("Mitarbeiter " + id + " nicht gefunden"); }
}
