package de.szut.lf8_starter.project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.Objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Assigns an employee to a project; the qualification ID serves as the employee's role.
 */
@Entity
@Table(
        name = "project_employee_assignments",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_project_employee_assignments_project_employee",
                columnNames = { "project_id", "employee_id" }))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectEmployeeAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ProjectEntity project;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "qualification_id", nullable = false)
    private Long qualificationId;

    /**
     * Creates an assignment of an employee to a project.
     *
     * @param project the project the employee is assigned to
     * @param employeeId the ID of the employee in the employee service
     * @param qualificationId the ID of the qualification used as role
     * @throws NullPointerException if any argument is null
     */
    public ProjectEmployeeAssignment(
            final ProjectEntity project, final Long employeeId, final Long qualificationId) {
        this.project = Objects.requireNonNull(project, "project must not be null");
        this.employeeId = Objects.requireNonNull(employeeId, "employeeId must not be null");
        this.qualificationId =
                Objects.requireNonNull(qualificationId, "qualificationId must not be null");
    }
}
