package de.szut.lf8_starter.project.api;

import java.time.LocalDate;
import java.util.Set;

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
