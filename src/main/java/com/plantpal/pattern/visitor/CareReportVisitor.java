package com.plantpal.pattern.visitor;
public interface CareReportVisitor {
    void visit(PlantReportEntry plant);
    void visit(TaskReportEntry task);
    void visit(NotificationReportEntry notification);
}
