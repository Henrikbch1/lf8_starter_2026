package de.szut.lf8_starter.project.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a project managed by the service.
 */
@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String designation;

    @Column(nullable = false)
    private Long responsibleEmployeeId;

    @Column(nullable = false)
    private Long customerId;

    private String customerContactName;

    @Column(length = 2000)
    private String goalComment;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate plannedEndDate;

    private LocalDate actualEndDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "project_required_qualifications",
            joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "qualification_id", nullable = false)
    private Set<Long> requiredQualificationIds = new HashSet<>();
}
