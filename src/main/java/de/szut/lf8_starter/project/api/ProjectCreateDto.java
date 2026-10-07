package de.szut.lf8_starter.project.api;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for creating a project.
 *
 * @param designation              name of the project
 * @param responsibleEmployeeId    id of the employee responsible for the project
 * @param customerId               id of the customer the project is carried out for
 * @param customerContactName      optional name of the contact person at the customer
 * @param goalComment              optional comment describing the project goal
 * @param startDate                planned start date
 * @param plannedEndDate           planned end date
 * @param requiredQualificationIds optional ids of the qualifications required for the project
 */
public record ProjectCreateDto(
        @NotBlank(message = "must not be blank")
        String designation,
        @NotNull(message = "must not be null")
        Long responsibleEmployeeId,
        @NotNull(message = "must not be null")
        Long customerId,
        String customerContactName,
        String goalComment,
        @NotNull(message = "must not be null")
        LocalDate startDate,
        @NotNull(message = "must not be null")
        LocalDate plannedEndDate,
        Set<@NotNull Long> requiredQualificationIds) {
}
