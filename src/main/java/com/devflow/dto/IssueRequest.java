package com.devflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class IssueRequest {
    @NotBlank @Size(max = 200)
    private String title;

    @Size(max = 5000)
    private String description;

    /** BUG / FEATURE / TASK / IMPROVEMENT */
    private String type = "TASK";

    /** HIGHEST / HIGH / MEDIUM / LOW / LOWEST */
    private String priority = "MEDIUM";

    private Long sprintId;
    private Long assigneeId;
}
