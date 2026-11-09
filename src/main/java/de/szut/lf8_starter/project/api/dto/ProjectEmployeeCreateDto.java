package de.szut.lf8_starter.project.api.dto;
import jakarta.validation.constraints.NotNull;

/**
 * Represents the request data required to assign an employee to a project.
 *
 * @param employeeId the ID of the employee to assign
 * @param qualificationId the ID of the qualification used as the employee's role
 */
public record ProjectEmployeeCreateDto(
        @NotNull Long employeeId,
        @NotNull Long qualificationId
) {
}
