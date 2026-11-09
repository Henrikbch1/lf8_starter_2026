package de.szut.lf8_starter.project.domain.exception;

/**
 * Indicates that an employee assignment conflicts with an existing assignment.
 */
public class ProjectEmployeeConflictException extends RuntimeException {

    /**
     * Creates an exception describing an employee assignment conflict.
     *
     * @param message the reason for the conflict
     */
    public ProjectEmployeeConflictException(String message) {
        super(message);
    }
}