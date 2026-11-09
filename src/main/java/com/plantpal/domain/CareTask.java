package com.plantpal.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "care_tasks")
public class CareTask {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "plant_id", nullable = false) private Plant plant;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private CareTaskType type;
    @Column(nullable = false, length = 120) private String title;
    @Column(nullable = false) private LocalDate dueDate;
    @Column(nullable = false) private boolean completed;

    protected CareTask() { }
    public CareTask(Plant plant, CareTaskType type, String title, LocalDate dueDate) {
        this.plant = plant; this.type = type; this.title = title;
        this.dueDate = dueDate; this.completed = false;
    }
    public Long getId() { return id; }
    public Plant getPlant() { return plant; }
    public CareTaskType getType() { return type; }
    public String getTitle() { return title; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setDueDate(LocalDate dueDate) {
        if (dueDate == null) throw new IllegalArgumentException("Due date required");
        this.dueDate = dueDate;
    }
}
