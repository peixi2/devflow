package com.devflow.repository;

import com.devflow.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {
    List<Issue> findByProjectId(Long projectId);
    List<Issue> findBySprintId(Long sprintId);
    List<Issue> findByAssigneeId(Long assigneeId);
    List<Issue> findByProjectIdAndStatus(Long projectId, String status);
    List<Issue> findByProjectIdAndStatusAndType(Long projectId, String status, String type);
}
