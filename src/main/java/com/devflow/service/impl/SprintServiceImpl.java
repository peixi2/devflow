package com.devflow.service.impl;

import com.devflow.dto.SprintRequest;
import com.devflow.entity.Sprint;
import com.devflow.repository.SprintRepository;
import com.devflow.service.SprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SprintServiceImpl implements SprintService {

    private final SprintRepository sprintRepo;

    @Override
    public Sprint create(Long projectId, SprintRequest req) {
        Sprint s = Sprint.builder()
                .projectId(projectId).name(req.getName()).goal(req.getGoal())
                .startDate(req.getStartDate()).endDate(req.getEndDate())
                .status("PLANNING").build();
        return sprintRepo.save(s);
    }

    @Override
    public Sprint update(Long sprintId, SprintRequest req) {
        Sprint s = sprintRepo.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint不存在"));
        s.setName(req.getName());
        s.setGoal(req.getGoal());
        s.setStartDate(req.getStartDate());
        s.setEndDate(req.getEndDate());
        return sprintRepo.save(s);
    }

    @Override
    public void delete(Long sprintId) {
        sprintRepo.deleteById(sprintId);
    }

    @Override
    public Sprint getById(Long sprintId) {
        return sprintRepo.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint不存在"));
    }

    @Override
    public List<Sprint> listByProject(Long projectId) {
        return sprintRepo.findByProjectId(projectId);
    }

    @Override
    public void startSprint(Long sprintId) {
        Sprint s = getById(sprintId);
        s.setStatus("ACTIVE");
        sprintRepo.save(s);
    }

    @Override
    public void completeSprint(Long sprintId) {
        Sprint s = getById(sprintId);
        s.setStatus("COMPLETED");
        sprintRepo.save(s);
    }
}
