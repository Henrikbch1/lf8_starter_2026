package de.szut.lf8_starter.project.api;

import java.time.LocalDate;
import java.util.Set;

/**
 * Response payload representing a stored project.
 *
 * @param id                       generated project id
 * @param designation              name of the project
 * @param responsibleEmployeeId    id of the employee responsible for the project
 * @param customerId               id of the customer the project is carried out for
 * @param customerContactName      name of the contact person at the customer, may be null
 * @param goalComment              comment describing the project goal, may be null
 * @param startDate                planned start date
 * @param plannedEndDate           planned end date
 * @param actualEndDate            actual end date, null while the project is not finished
 * @param requiredQualificationIds ids of the qualifications required for the project
 */
public record ProjectGetDto(
        Long id,
        String designation,
        Long responsibleEmployeeId,
        Long customerId,
        String customerContactName,
        String goalComment,
        LocalDate startDate,
        LocalDate plannedEndDate,
        LocalDate actualEndDate,
        Set<Long> requiredQualificationIds) {
}
