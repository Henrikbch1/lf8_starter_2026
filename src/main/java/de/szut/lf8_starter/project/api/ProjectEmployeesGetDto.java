package de.szut.lf8_starter.project.api;

import java.util.List;
import java.util.Objects;

/**
 * Response payload representing the employees assigned to a project.
 *
 * @param projectId   id of the project
 * @param designation name of the project
 * @param employees   employees assigned to the project, empty if none are assigned
 */
public record ProjectEmployeesGetDto(
        Long projectId,
        String designation,
        List<ProjectEmployeeGetDto> employees) {

    /**
     * Creates the response payload and stores an immutable copy of the employee list.
     *
     * @throws NullPointerException if the employee list is null
     */
    public ProjectEmployeesGetDto {
        employees = List.copyOf(Objects.requireNonNull(employees, "employees must not be null"));
    }
}
