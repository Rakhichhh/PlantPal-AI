package com.plantpal.api;

import com.plantpal.domain.Plant;
import com.plantpal.service.PlantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PlantController {
    private final PlantService service;
    public PlantController(PlantService service) { this.service = service; }
    @GetMapping("/plants") public List<Plant> all() { return service.all(); }
    @GetMapping("/plants/{id}") public Plant get(@PathVariable Long id) { return service.get(id); }
    @PostMapping("/plants") @ResponseStatus(HttpStatus.CREATED)
    public Plant add(@Valid @RequestBody PlantRequest request) { return service.add(request); }
    @PutMapping("/plants/{id}") public Plant edit(@PathVariable Long id, @Valid @RequestBody PlantRequest request) { return service.edit(id,request); }
    @DeleteMapping("/plants/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
    @GetMapping("/tasks") public List<TaskResponse> tasks() { return service.allTasks().stream().map(TaskResponse::from).toList(); }
    @GetMapping("/plants/{id}/tasks") public List<TaskResponse> tasksFor(@PathVariable Long id) { return service.tasksFor(id).stream().map(TaskResponse::from).toList(); }
    @PatchMapping("/tasks/{id}/completion") public TaskResponse mark(@PathVariable Long id, @RequestBody CompletionRequest r) {
        return TaskResponse.from(service.completeTask(id,r.completed()));
    }
    public record CompletionRequest(boolean completed) { }
}
