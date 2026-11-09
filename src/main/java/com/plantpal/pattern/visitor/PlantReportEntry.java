package com.plantpal.pattern.visitor;
import com.plantpal.domain.Plant;
import java.util.Objects;
public record PlantReportEntry(Plant plant) implements CareReportElement {
    public PlantReportEntry { Objects.requireNonNull(plant); }
    @Override public void accept(CareReportVisitor visitor) { visitor.visit(this); }
}
