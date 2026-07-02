package com.devflow.service;

import com.devflow.dto.SprintRequest;
import com.devflow.entity.Sprint;
import java.util.List;

public interface SprintService {
    Sprint create(Long projectId, SprintRequest req);
    Sprint update(Long sprintId, SprintRequest req);
    void delete(Long sprintId);
    Sprint getById(Long sprintId);
    List<Sprint> listByProject(Long projectId);
    void startSprint(Long sprintId);
    void completeSprint(Long sprintId);
}
