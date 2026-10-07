package de.szut.lf8_starter.project.domain;

import java.time.LocalDate;

/**
 * Signals that a project's start date is after its planned end date.
 */
public final class InvalidProjectPeriodException extends RuntimeException {

    /**
     * Creates an exception for a project period with an invalid date order.
     *
     * @param startDate the project's start date
     * @param plannedEndDate the project's planned end date
     */
    public InvalidProjectPeriodException(final LocalDate startDate, final LocalDate plannedEndDate) {
        super("Start date " + startDate + " is after the planned end date " + plannedEndDate);
    }
}
