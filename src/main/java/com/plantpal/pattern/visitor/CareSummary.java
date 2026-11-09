package com.plantpal.pattern.visitor;
import java.time.LocalDate;
public record CareSummary(LocalDate generatedOn, int plants, int tasks, int completedTasks,
                          int pendingTasks, int overdueTasks, int notifications, int unreadNotifications,
                          double completionRatePercent) { }
