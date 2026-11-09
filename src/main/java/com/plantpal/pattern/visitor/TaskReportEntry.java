package com.plantpal.pattern.visitor;
import com.plantpal.domain.CareTask;
import java.util.Objects;
public record TaskReportEntry(CareTask task) implements CareReportElement {
    public TaskReportEntry { Objects.requireNonNull(task); }
    @Override public void accept(CareReportVisitor visitor) { visitor.visit(this); }
}
