package com.recruitment.temperatures.temperatures.jpa;

import com.recruitment.temperatures.RecruitmentChallengeCSVProperties;
import com.recruitment.temperatures.models.GitDiffLine;
import com.recruitment.temperatures.models.TemperatureCreator;
import org.springframework.stereotype.Component;




@Component
public class LineChangesHandlerImpl implements LineChangesHandler {
    private final TemperatureRepository temperatureRepository;
    private final RecruitmentChallengeCSVProperties recruitmentChallengeCSVProperties;

    public LineChangesHandlerImpl(TemperatureRepository temperatureRepository, RecruitmentChallengeCSVProperties recruitmentChallengeCSVProperties) {
        this.temperatureRepository = temperatureRepository;
        this.recruitmentChallengeCSVProperties = recruitmentChallengeCSVProperties;
    }

    private void insert(String line) {
        temperatureRepository.save(new TemperatureCreator(line, recruitmentChallengeCSVProperties.getDelimiter()).create());
    }

    private void delete(String line) {
        Temperature temperature = new TemperatureCreator(line, recruitmentChallengeCSVProperties.getDelimiter()).create();
        temperatureRepository.deleteTemperatureByCityAndDateAndValue(temperature.getCity(), temperature.getDate(), temperature.getValue());
    }

    @Override
    public void handle(GitDiffLine csvLine) {
        switch (csvLine.line().charAt(0)) {
            case '+':
                insert(csvLine.line().substring(1));
                break;
            case '-':
                delete(csvLine.line().substring(1));
                break;
        }
    }
}
