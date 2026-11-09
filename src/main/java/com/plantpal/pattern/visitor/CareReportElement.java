package com.plantpal.pattern.visitor;
/** Visitable entry point for the user-owned reporting Visitor pattern. */
public interface CareReportElement {
    void accept(CareReportVisitor visitor);
}
