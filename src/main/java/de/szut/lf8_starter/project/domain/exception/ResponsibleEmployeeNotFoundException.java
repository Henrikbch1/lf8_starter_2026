package de.szut.lf8_starter.project.domain.exception;

/**
 * Signals that a changed responsible employee does not exist.
 */
public final class ResponsibleEmployeeNotFoundException extends RuntimeException {

    /**
     * Creates an exception for an unknown responsible employee.
     *
     * @param employeeId the ID of the missing employee
     */
    public ResponsibleEmployeeNotFoundException(final Long employeeId) {
        super("Responsible employee with ID " + employeeId + " not found");
    }
}
