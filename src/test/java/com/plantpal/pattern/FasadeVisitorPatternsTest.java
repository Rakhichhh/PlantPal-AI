package com.plantpal.pattern;
import com.plantpal.api.PlantRequest;
import com.plantpal.domain.*;
import com.plantpal.pattern.facade.PlantCareFacade;
import com.plantpal.pattern.visitor.*;
import com.plantpal.repository.*;
import com.plantpal.service.*;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class Student4PatternsTest {
 private final Plant sample = new Plant("Monstera", "Monstera deliciosa", "Living room", LightLevel.INDIRECT, 7, "", "", Instant.parse("2026-01-01T00:00:00Z"));
 @Test void visitorAggregatesThreeKinds() {
  CareTask due = new CareTask(sample, CareTaskType.SOIL_CHECK, "Check soil", LocalDate.of(2026, 1, 2));
  CareTask done = new CareTask(sample, CareTaskType.INSPECT, "Inspect", LocalDate.of(2026, 1, 4));done.setCompleted(true);
  CareNotification notification = new CareNotification(1L,"Care due",Instant.now());
  CareStatisticsVisitor visitor = new CareStatisticsVisitor(LocalDate.of(2026, 1, 5));
  List.of(new PlantReportEntry(sample),new TaskReportEntry(due),new TaskReportEntry(done),new NotificationReportEntry(notification)).forEach(e->e.accept(visitor));
  CareSummary summary=visitor.summary();
  assertEquals(1,summary.plants());assertEquals(2,summary.tasks());assertEquals(1,summary.completedTasks());
  assertEquals(1,summary.pendingTasks());assertEquals(1,summary.overdueTasks());
  assertEquals(1,summary.unreadNotifications());assertEquals(50.0,summary.completionRatePercent());
 }
 @Test void emptyVisitorReturnsSafeZeroPercentage() {
  CareSummary s=new CareStatisticsVisitor(LocalDate.of(2026,1,1)).summary();
  assertEquals(0,s.tasks());assertEquals(0.0,s.completionRatePercent());
 }
 @Test void visitorDoesNotCountCompletedTasksOverdue() {
  CareTask task=new CareTask(sample,CareTaskType.SOIL_CHECK,"Soil",LocalDate.of(2026,1,1));task.setCompleted(true);
  CareStatisticsVisitor visitor=new CareStatisticsVisitor(LocalDate.of(2026,2,1));
  new TaskReportEntry(task).accept(visitor);assertEquals(0,visitor.summary().overdueTasks());
 }
 @Test void facadeDelegatesCreation() {
  PlantService service=mock(PlantService.class);
  PlantCareFacade facade=new PlantCareFacade(service,mock(CarePlanningService.class),mock(PlantRepository.class),mock(CareTaskRepository.class),mock(CareNotificationRepository.class));
  PlantRequest request=mock(PlantRequest.class);when(service.add(request)).thenReturn(sample);
  assertSame(sample,facade.registerPlant(request));verify(service).add(request);
 }
 @Test void facadeDelegatesStrategyChange() {
  CarePlanningService planner=mock(CarePlanningService.class);
  PlantCareFacade facade=new PlantCareFacade(mock(PlantService.class),planner,mock(PlantRepository.class),mock(CareTaskRepository.class),mock(CareNotificationRepository.class));
  CareSettings settings=new CareSettings(11L,CareMode.CONSERVATIVE);when(planner.changeMode(11L,CareMode.CONSERVATIVE)).thenReturn(settings);
  assertEquals(CareMode.CONSERVATIVE,facade.chooseCareMode(11L,CareMode.CONSERVATIVE).getMode());
 }
 @Test void facadeSummaryPullsRealRepositories() {
  PlantRepository plants=mock(PlantRepository.class);CareTaskRepository tasks=mock(CareTaskRepository.class);CareNotificationRepository notes=mock(CareNotificationRepository.class);
  when(plants.findAll()).thenReturn(List.of(sample));when(tasks.findAll()).thenReturn(List.of());when(notes.findAll()).thenReturn(List.of());
  Clock clock=Clock.fixed(Instant.parse("2026-01-08T00:00:00Z"),ZoneOffset.UTC);
  PlantCareFacade facade=new PlantCareFacade(mock(PlantService.class),mock(CarePlanningService.class),plants,tasks,notes,clock);
  CareSummary summary=facade.careSummary();assertEquals(1,summary.plants());assertEquals(LocalDate.of(2026,1,8),summary.generatedOn());
 }
 @Test void csvQuotesAndPreventsFormulas() {
  Plant formula = new Plant("=SUM(1,1)","Species","Room",LightLevel.LOW,7,"","",Instant.now());
  CareTask task=new CareTask(formula,CareTaskType.SOIL_CHECK,"Check, soil",LocalDate.of(2026,1,9));
  CareSummary summary=new CareSummary(LocalDate.of(2026,1,8),1,1,0,1,0,0,0,0);
  String csv=CareReportExportService.toCsv(summary,List.of(task));
  assertTrue(csv.contains("\"'=SUM(1,1)\""));assertTrue(csv.contains("\"Check, soil\""));
 }
 @Test void nullVisitElementRejected() { assertThrows(NullPointerException.class,()->new PlantReportEntry(null)); }
}
