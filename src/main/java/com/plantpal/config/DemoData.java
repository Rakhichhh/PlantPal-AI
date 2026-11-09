package com.plantpal.config;

import com.plantpal.api.PlantRequest;
import com.plantpal.domain.LightLevel;
import com.plantpal.repository.PlantRepository;
import com.plantpal.service.PlantService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoData {
    @Bean CommandLineRunner seed(PlantRepository plants, PlantService service) {
        return args -> {
            if (plants.count() > 0) return;
            service.add(new PlantRequest("Monstera", "Monstera deliciosa", "Living Room", LightLevel.INDIRECT,
                    7, "Check soil moisture before watering", ""));
            service.add(new PlantRequest("Snake Plant", "Dracaena trifasciata", "Bedroom", LightLevel.LOW,
                    14, "Avoid overwatering", ""));
            service.add(new PlantRequest("Pothos", "Epipremnum aureum", "Office", LightLevel.INDIRECT,
                    8, "Rotate pot occasionally", ""));
        };
    }
}
