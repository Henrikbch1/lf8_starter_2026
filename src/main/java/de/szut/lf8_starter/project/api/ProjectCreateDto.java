package de.szut.lf8_starter.project.api;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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
