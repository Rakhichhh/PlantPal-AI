package com.plantpal.repository;
import com.plantpal.domain.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PlantRepository extends JpaRepository<Plant, Long> { }
