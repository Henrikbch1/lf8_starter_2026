package de.szut.lf8_starter.employee;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public record QualificationDto(long id, String skill) {}
