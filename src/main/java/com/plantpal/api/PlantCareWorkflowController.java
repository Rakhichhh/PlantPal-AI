package com.plantpal.api;
import com.plantpal.domain.*;
import com.plantpal.pattern.facade.PlantCareFacade;
import com.plantpal.pattern.visitor.CareSummary;
import com.plantpal.service.CareReportExportService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
/** Student 4 routes used by real web interface. */
@RestController
@RequestMapping("/api/workflows")
public class PlantCareWorkflowController {
    private final PlantCareFacade facade;
    private final CareReportExportService export;
    public PlantCareWorkflowController(PlantCareFacade facade, CareReportExportService export) {
        this.facade=facade; this.export=export;
    }
    @PostMapping("/plants") @ResponseStatus(HttpStatus.CREATED)
    public Plant addPlant(@Valid @RequestBody PlantRequest request) { return facade.registerPlant(request); }
    @PutMapping("/plants/{id}/care-mode")
    public CareSettings mode(@PathVariable Long id, @RequestBody ModeRequest request) {
        return facade.chooseCareMode(id,request.mode());
    }
    @PatchMapping("/tasks/{id}/complete")
    public TaskResponse complete(@PathVariable Long id) { return TaskResponse.from(facade.completeCareTask(id)); }
    @GetMapping("/summary") public CareSummary summary() { return facade.careSummary(); }
    @GetMapping(value="/report.csv",produces="text/csv")
    public ResponseEntity<byte[]> download() {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=plantpal-care-report.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(("\uFEFF"+export.exportCsv()).getBytes(StandardCharsets.UTF_8));
    }
    public record ModeRequest(CareMode mode) { }
}
