package com.plantpal.pattern.visitor;
import java.time.LocalDate;
import java.util.Objects;
/** Concrete Visitor: aggregates statistics without placing report logic in domain entities. */
public class CareStatisticsVisitor implements CareReportVisitor {
    private final LocalDate today;
    private int plants, tasks, completed, overdue, notifications, unread;
    public CareStatisticsVisitor(LocalDate today) { this.today = Objects.requireNonNull(today); }
    @Override public void visit(PlantReportEntry entry) { plants++; }
    @Override public void visit(TaskReportEntry entry) {
        tasks++;
        if (entry.task().isCompleted()) completed++;
        else if (entry.task().getDueDate().isBefore(today)) overdue++;
    }
    @Override public void visit(NotificationReportEntry entry) {
        notifications++;
        if (!entry.notification().isRead()) unread++;
    }
    public CareSummary summary() {
        double percentage = tasks == 0 ? 0 : Math.round(completed * 1000.0 / tasks) / 10.0;
        return new CareSummary(today, plants, tasks, completed, tasks-completed, overdue,
                notifications, unread, percentage);
    }
}
