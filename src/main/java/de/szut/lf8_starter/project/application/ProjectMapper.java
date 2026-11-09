package de.szut.lf8_starter.project.application;

import java.util.HashSet;

import de.szut.lf8_starter.project.api.dto.ProjectCreateDto;
import de.szut.lf8_starter.project.api.dto.ProjectGetDto;
import de.szut.lf8_starter.project.domain.ProjectEntity;
import org.springframework.stereotype.Component;

/**
 * Converts between project DTOs and the project entity.
 */
@Component
public class ProjectMapper {

    /**
     * Creates a new, not yet persisted entity from a create request.
     *
     * @param dto the validated create request
     * @return a new entity without id and actual end date
     */
    public ProjectEntity fromCreate(ProjectCreateDto dto) {
        ProjectEntity entity = new ProjectEntity();
        entity.setDesignation(dto.designation());
        entity.setResponsibleEmployeeId(dto.responsibleEmployeeId());
        entity.setCustomerId(dto.customerId());
        entity.setCustomerContactName(dto.customerContactName());
        entity.setGoalComment(dto.goalComment());
        entity.setStartDate(dto.startDate());
        entity.setPlannedEndDate(dto.plannedEndDate());
        entity.setRequiredQualificationIds(dto.requiredQualificationIds() == null
                ? new HashSet<>()
                : new HashSet<>(dto.requiredQualificationIds()));
        return entity;
    }

    /**
     * Copies editable project data while preserving the stored ID and actual end date.
     *
     * @param project the stored project to update
     * @param changes the validated editable project data
     */
    public void applyUpdate(ProjectEntity project, ProjectEntity changes) {
        project.setDesignation(changes.getDesignation());
        project.setResponsibleEmployeeId(changes.getResponsibleEmployeeId());
        project.setCustomerId(changes.getCustomerId());
        project.setCustomerContactName(changes.getCustomerContactName());
        project.setGoalComment(changes.getGoalComment());
        project.setStartDate(changes.getStartDate());
        project.setPlannedEndDate(changes.getPlannedEndDate());
        project.setRequiredQualificationIds(new HashSet<>(changes.getRequiredQualificationIds()));
    }

    /**
     * Converts an entity into its response representation.
     *
     * @param entity the project entity
     * @return the response DTO
     */
    public ProjectGetDto toDto(ProjectEntity entity) {
        return new ProjectGetDto(
                entity.getId(),
                entity.getDesignation(),
                entity.getResponsibleEmployeeId(),
                entity.getCustomerId(),
                entity.getCustomerContactName(),
                entity.getGoalComment(),
                entity.getStartDate(),
                entity.getPlannedEndDate(),
                entity.getActualEndDate(),
                entity.getRequiredQualificationIds());
    }
}
