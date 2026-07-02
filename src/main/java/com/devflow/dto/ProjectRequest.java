package com.devflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjectRequest {
    @NotBlank @Size(max = 100)
    private String name;

    @Size(max = 2000)
    private String description;
}
