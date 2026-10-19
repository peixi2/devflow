package com.devflow.config;

import com.devflow.entity.Issue;
import com.devflow.entity.Project;
import com.devflow.entity.ProjectMember;
import com.devflow.entity.Sprint;
import com.devflow.entity.User;
import com.devflow.repository.IssueRepository;
import com.devflow.repository.ProjectMemberRepository;
import com.devflow.repository.ProjectRepository;
import com.devflow.repository.SprintRepository;
import com.devflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.demo.seed-enabled", havingValue = "true")
public class DemoDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final SprintRepository sprintRepository;
    private final IssueRepository issueRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.demo.username}")
    private String username;

    @Value("${app.demo.password}")
    private String password;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.existsByUsername(username)) {
            return;
        }

        User user = userRepository.save(User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname("DevFlow Demo")
                .status(1)
                .build());

        Project project = projectRepository.save(Project.builder()
                .name("DevFlow 2.0")
                .description("AI 驱动的软件项目管理平台演示项目")
                .ownerId(user.getId())
                .status("ACTIVE")
                .build());

        memberRepository.save(ProjectMember.builder()
                .projectId(project.getId())
                .userId(user.getId())
                .role("PM")
                .build());

        Sprint sprint = sprintRepository.save(Sprint.builder()
                .projectId(project.getId())
                .name("Sprint 1 - 公网演示")
                .goal("完成核心流程并验证 AI 辅助能力")
                .startDate(LocalDate.now().minusDays(3))
                .endDate(LocalDate.now().plusDays(11))
                .status("ACTIVE")
                .build());

        issueRepository.save(Issue.builder()
                .projectId(project.getId())
                .sprintId(sprint.getId())
                .title("实现项目看板与状态流转")
                .description("支持 TODO 到 DONE 的完整工作流")
                .type("FEATURE")
                .priority("HIGH")
                .status("IN_PROGRESS")
                .assigneeId(user.getId())
                .reporterId(user.getId())
                .build());

        issueRepository.save(Issue.builder()
                .projectId(project.getId())
                .sprintId(sprint.getId())
                .title("接入 DeepSeek 自动分类")
                .description("根据标题和描述自动识别 Issue 类型、优先级与标签")
                .type("TASK")
                .priority("MEDIUM")
                .status("TODO")
                .assigneeId(user.getId())
                .reporterId(user.getId())
                .build());

        issueRepository.save(Issue.builder()
                .projectId(project.getId())
                .sprintId(sprint.getId())
                .title("修复登录过期后的错误提示")
                .description("JWT 过期时清理本地状态并返回登录页")
                .type("BUG")
                .priority("HIGH")
                .status("DONE")
                .assigneeId(user.getId())
                .reporterId(user.getId())
                .build());

        log.info("Demo data initialized for user '{}'", username);
    }
}
