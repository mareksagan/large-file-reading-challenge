package com.recruitment.temperatures.temperatures.jpa;

import com.recruitment.temperatures.models.AverageTemperature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface TemperatureRepository extends JpaRepository<Temperature, Long> {
    @Transactional
    void deleteTemperatureByCityAndDateAndValue(String city, Date date, Double value);
    @Query(value = "SELECT NEXTVAL('TEMPERATURES_SEQ')", nativeQuery = true)
    Long getNextSequence();

    @Query(value = """ 
            select new com.recruitment.temperatures.models.AverageTemperature( EXTRACT(YEAR FROM tmp.date), avg(tmp.value))
            from Temperature tmp
            WHERE tmp.city = ?1
            GROUP BY tmp.city, EXTRACT( YEAR FROM tmp.date) """)
    List<AverageTemperature> averageGroupByYear(String city);

}
