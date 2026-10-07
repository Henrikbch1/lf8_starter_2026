package de.szut.lf8_starter.project.mapping;

import java.util.HashSet;

import de.szut.lf8_starter.project.api.ProjectCreateDto;
import de.szut.lf8_starter.project.api.ProjectGetDto;
import de.szut.lf8_starter.project.persistence.ProjectEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

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
