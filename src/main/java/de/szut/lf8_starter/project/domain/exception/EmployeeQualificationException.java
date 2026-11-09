package de.szut.lf8_starter.project.domain.exception;

/**
 * Indicates that an employee does not have the required qualification.
 */
public class EmployeeQualificationException extends RuntimeException {

    /**
     * Creates an exception for a missing employee qualification.
     *
     * @param message the reason why the qualification is invalid
     */
    public EmployeeQualificationException(String message) {
        super(message);
    }
}