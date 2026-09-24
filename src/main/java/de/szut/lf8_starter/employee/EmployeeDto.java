package de.szut.lf8_starter.employee;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EmployeeDto(long id, String firstName, String lastName, List<QualificationDto> skillSet) {
}
