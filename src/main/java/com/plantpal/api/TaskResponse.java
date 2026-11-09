package com.plantpal.api;
import com.plantpal.domain.*;
import java.time.LocalDate;
public record TaskResponse(Long id, Long plantId, CareTaskType type, String title, LocalDate dueDate, boolean completed) {
    public static TaskResponse from(CareTask t) {
        return new TaskResponse(t.getId(),t.getPlant().getId(),t.getType(),t.getTitle(),t.getDueDate(),t.isCompleted());
    }
}
