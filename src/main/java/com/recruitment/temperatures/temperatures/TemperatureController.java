package com.recruitment.temperatures.temperatures;

import com.recruitment.temperatures.models.AverageTemperature;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("temperatures")
public class TemperatureController {
    private final TemperatureRepository temperatureRepository;

    public TemperatureController(TemperatureRepository temperatureRepository) {
        this.temperatureRepository = temperatureRepository;
    }

    @GetMapping("avg/{city}")
    public List<AverageTemperature> findByCity(@PathVariable String city) {
        return temperatureRepository.averageGroupByYear(city);
    }
}
