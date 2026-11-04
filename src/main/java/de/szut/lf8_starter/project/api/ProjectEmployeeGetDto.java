package de.szut.lf8_starter.project.api;

import de.szut.lf8_starter.project.domain.ProjectEmployeeAssignment;

/**
 * Response payload representing an employee assigned to a project.
 *
 * @param employeeId id of the assigned employee in the employee service
 * @param role       id of the qualification the employee works as on the project
 */
public record ProjectEmployeeGetDto(Long employeeId, Long role) {

    /**
     * Converts an employee assignment into its response representation.
     *
     * @param assignment the assignment of an employee to a project
     * @return the response DTO
     */
    public static ProjectEmployeeGetDto from(final ProjectEmployeeAssignment assignment) {
        return new ProjectEmployeeGetDto(
                assignment.getEmployeeId(),
                assignment.getQualificationId());
    }
}
