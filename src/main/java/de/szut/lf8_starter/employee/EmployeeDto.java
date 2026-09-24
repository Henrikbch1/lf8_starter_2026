package de.szut.lf8_starter.employee;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public record EmployeeDto(long id, String firstName, String lastName, List<QualificationDto> skillSet) {}
