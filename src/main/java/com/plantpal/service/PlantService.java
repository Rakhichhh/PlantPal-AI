package com.plantpal.service;

import com.plantpal.api.PlantRequest;
import com.plantpal.domain.*;
import com.plantpal.pattern.builder.PlantBuilder;
import com.plantpal.pattern.factory.CareTaskCreator;
import com.plantpal.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.*;

@Service
public class PlantService {
    private final PlantRepository plants;
    private final CareTaskRepository tasks;
    private final Map<CareTaskType, CareTaskCreator> creators = new EnumMap<>(CareTaskType.class);

    public PlantService(PlantRepository plants, CareTaskRepository tasks, List<CareTaskCreator> availableCreators) {
        this.plants = plants; this.tasks = tasks;
        for (CareTaskCreator c : availableCreators) {
            if (creators.putIfAbsent(c.supportedType(), c) != null)
                throw new IllegalStateException("Duplicate task creator for " + c.supportedType());
        }
        if (creators.size() != CareTaskType.values().length)
            throw new IllegalStateException("A task creator is missing");
    }
    @Transactional(readOnly = true)
    public List<Plant> all() { return plants.findAll(); }
    @Transactional(readOnly = true)
    public Plant get(Long id) { return plants.findById(id).orElseThrow(() -> missing("Plant", id)); }

    /** Student 1 demonstration: Builder constructs Plant; Factory Method creates its initial tasks. */
    @Transactional
    public Plant add(PlantRequest r) {
        Plant plant = new PlantBuilder().nickname(r.nickname()).species(r.species()).room(r.room())
                .lightLevel(r.lightLevel()).wateringIntervalDays(r.wateringIntervalDays())
                .notes(r.notes()).photoUrl(r.photoUrl()).build();
        Plant saved = plants.save(plant);
        LocalDate today = LocalDate.now();
        for (CareTaskCreator creator : creators.values()) tasks.save(creator.schedule(saved, today));
        return saved;
    }
    @Transactional
    public Plant edit(Long id, PlantRequest r) {
        Plant existing = get(id);
        // Reuse the Builder's business validation for updates.
        Plant validated = new PlantBuilder().nickname(r.nickname()).species(r.species()).room(r.room())
                .lightLevel(r.lightLevel()).wateringIntervalDays(r.wateringIntervalDays())
                .notes(r.notes()).photoUrl(r.photoUrl()).build();
        existing.update(validated.getNickname(),validated.getSpecies(),validated.getRoom(),
                validated.getLightLevel(),validated.getWateringIntervalDays(),validated.getNotes(), validated.getPhotoUrl());
        return plants.save(existing);
    }
    @Transactional
    public void delete(Long id) {
        get(id);
        tasks.deleteByPlantId(id);
        plants.deleteById(id);
    }
    @Transactional(readOnly = true)
    public List<CareTask> allTasks() { return tasks.findAllByOrderByDueDateAsc(); }
    @Transactional(readOnly = true)
    public List<CareTask> tasksFor(Long id) { get(id); return tasks.findByPlantIdOrderByDueDateAsc(id); }
    @Transactional
    public CareTask completeTask(Long id, boolean completed) {
        CareTask t = tasks.findById(id).orElseThrow(() -> missing("Task",id));
        t.setCompleted(completed); return tasks.save(t);
    }
    private ResponseStatusException missing(String type, Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, type + " " + id + " not found");
    }
}
