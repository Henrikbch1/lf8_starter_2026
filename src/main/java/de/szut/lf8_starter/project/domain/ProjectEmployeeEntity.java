package de.szut.lf8_starter.project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "project_employees")
@Getter
@Setter
@NoArgsConstructor
public class ProjectEmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The project to which the employee is assigned.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    /**
     * The ID of the employee managed by the Employee-Service.
     */
    @Column(name = "employee_id",nullable = false)
    private Long employeeId;

    /**
     * The ID of the qualification used as the employee's project role.
     */
    @Column(name = "qualification_id",nullable = false)
    private Long qualificationId;
}
