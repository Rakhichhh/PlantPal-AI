package com.plantpal.pattern.visitor;
import com.plantpal.domain.CareNotification;
import java.util.Objects;
public record NotificationReportEntry(CareNotification notification) implements CareReportElement {
    public NotificationReportEntry { Objects.requireNonNull(notification); }
    @Override public void accept(CareReportVisitor visitor) { visitor.visit(this); }
}
