package com.plantpal.pattern.facade;
import com.plantpal.api.PlantRequest;
import com.plantpal.domain.*;
import com.plantpal.pattern.visitor.*;
import com.plantpal.repository.*;
import com.plantpal.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
/** Facade: one simple interface orchestrates the plant-care workflow and reports. */
@Service
public class PlantCareFacade {
    private final PlantService plantService;
    private final CarePlanningService planningService;
    private final PlantRepository plants;
    private final CareTaskRepository tasks;
    private final CareNotificationRepository notifications;
    private final Clock clock;
    public PlantCareFacade(PlantService plantService, CarePlanningService planningService,
                           PlantRepository plants, CareTaskRepository tasks,
                           CareNotificationRepository notifications) {
        this(plantService, planningService, plants, tasks, notifications, Clock.systemDefaultZone());
    }
    // Package-independent overload for isolated tests and clock-controlled reports.
    public PlantCareFacade(PlantService plantService, CarePlanningService planningService,
                           PlantRepository plants, CareTaskRepository tasks,
                           CareNotificationRepository notifications, Clock clock) {
        this.plantService=plantService; this.planningService=planningService;
        this.plants=plants; this.tasks=tasks; this.notifications=notifications; this.clock=clock;
    }
    /** Uses Student 1 Builder + Factory and Student 2 Observer through PlantService. */
    @Transactional
    public Plant registerPlant(PlantRequest request) { return plantService.add(request); }
    /** User-visible strategy switch, then Observer recalculates schedule. */
    @Transactional
    public CareSettings chooseCareMode(Long plantId, CareMode mode) {
        return planningService.changeMode(plantId, mode);
    }
    @Transactional
    public CareTask completeCareTask(Long taskId) { return plantService.completeTask(taskId, true); }
    /** Uses double dispatch across 3 domain-derived entry types. */
    @Transactional(readOnly = true)
    public CareSummary careSummary() {
        CareStatisticsVisitor visitor = new CareStatisticsVisitor(LocalDate.now(clock));
        plants.findAll().stream().map(PlantReportEntry::new).forEach(e -> e.accept(visitor));
        tasks.findAll().stream().map(TaskReportEntry::new).forEach(e -> e.accept(visitor));
        notifications.findAll().stream().map(NotificationReportEntry::new).forEach(e -> e.accept(visitor));
        return visitor.summary();
    }
    @Transactional(readOnly = true)
    public List<CareTask> careTasks() { return tasks.findAllByOrderByDueDateAsc(); }
}
