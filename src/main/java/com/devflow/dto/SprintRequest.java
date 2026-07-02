package com.devflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class SprintRequest {
    @NotBlank @Size(max = 100)
    private String name;

    @Size(max = 2000)
    private String goal;

    private LocalDate startDate;
    private LocalDate endDate;
}
