package com.plantpal.service;
import com.plantpal.domain.CareTask;
import com.plantpal.pattern.facade.PlantCareFacade;
import com.plantpal.pattern.visitor.CareSummary;
import org.springframework.stereotype.Service;
import java.util.List;
/** CSV report of a persisted care plan, safe for spreadsheet programs. */
@Service
public class CareReportExportService {
    private final PlantCareFacade facade;
    public CareReportExportService(PlantCareFacade facade) { this.facade=facade; }
    public String exportCsv() { return toCsv(facade.careSummary(), facade.careTasks()); }
    public static String toCsv(CareSummary summary, List<CareTask> tasks) {
        StringBuilder out = new StringBuilder("PlantPal AI Care Report\r\n");
        out.append("Generated on,").append(summary.generatedOn()).append("\r\n");
        out.append("Plants,").append(summary.plants()).append("\r\n");
        out.append("Tasks,").append(summary.tasks()).append("\r\n");
        out.append("Completed,").append(summary.completedTasks()).append("\r\n");
        out.append("Pending,").append(summary.pendingTasks()).append("\r\n");
        out.append("Overdue,").append(summary.overdueTasks()).append("\r\n");
        out.append("Completion rate (%),").append(summary.completionRatePercent()).append("\r\n\r\n");
        out.append("Plant,Task,Type,Due date,Completed\r\n");
        for (CareTask task: tasks) {
            out.append(escape(task.getPlant().getNickname())).append(',')
               .append(escape(task.getTitle())).append(',')
               .append(task.getType()).append(',').append(task.getDueDate()).append(',')
               .append(task.isCompleted()).append("\r\n");
        }
        return out.toString();
    }
    /** Quotes CSV and prevents formula interpretation in spreadsheet software. */
    static String escape(String value) {
        if (value == null) return "\"\"";
        String sanitized = value;
        if (!sanitized.isBlank() && "=+-@".indexOf(sanitized.stripLeading().charAt(0)) >= 0)
            sanitized = "'" + sanitized;
        return "\"" + sanitized.replace("\"", "\"\"") + "\"";
    }
}
