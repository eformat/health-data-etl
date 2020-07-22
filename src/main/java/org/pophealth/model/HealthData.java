package org.pophealth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import lombok.Data;

@Data
public class HealthData {

    private Integer steps;
    private Integer activeMinutes;
    private Integer age;
    private Double sleepRpms;
    private Double sleepHours;
    private Double sleepAvgRpms;
    private Double heartRateBpm;
    private LocalDate measurementDate;

    @JsonIgnore
    private long timeNanos;

    public HealthData(){

    }
}
