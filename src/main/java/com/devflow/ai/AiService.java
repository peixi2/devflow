package com.devflow.ai;

import com.devflow.entity.*;
import com.devflow.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI 服务——DeepSeek 驱动的智能分析
 * 直接 HTTP 调用 DeepSeek API（比 Spring AI 适配层更稳定）
 */
@Slf4j
@Service
public class AiService {

    private final RestTemplate restTemplate;
    private final IssueRepository issueRepo;
    private final IssueCommentRepository commentRepo;
    private final ProjectRepository projectRepo;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String apiUrl;

    private static final String MODEL = "deepseek-chat";

    public AiService(IssueRepository issueRepo, IssueCommentRepository commentRepo,
                     ProjectRepository projectRepo,
                     @Value("${spring.ai.openai.api-key}") String apiKey) {
        this.issueRepo = issueRepo;
        this.commentRepo = commentRepo;
        this.projectRepo = projectRepo;
        this.apiKey = apiKey;
        this.apiUrl = "https://api.deepseek.com/v1/chat/completions";
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /** Issue 自动分类 */
    public Map<String, Object> classifyIssue(Long issueId) {
        Issue issue = issueRepo.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue不存在"));

        String prompt = String.format("""
            你是一个软件项目管理专家。分析以下Issue，只返回JSON（不要markdown包裹）：
            {"type":"BUG|FEATURE|TASK|IMPROVEMENT","priority":"HIGHEST|HIGH|MEDIUM|LOW|LOWEST","tags":["标签"],"reason":"一句话理由"}
            Issue标题: %s
            Issue描述: %s""",
            issue.getTitle(),
            issue.getDescription() != null ? issue.getDescription() : "无");

        String result = callDeepSeek(prompt);
        try {
            String json = extractJson(result);
            Map<String, Object> analysis = objectMapper.readValue(json,
                    new TypeReference<Map<String, Object>>() {});

            issue.setType((String) analysis.getOrDefault("type", "TASK"));
            issue.setPriority((String) analysis.getOrDefault("priority", "MEDIUM"));
            @SuppressWarnings("unchecked")
            List<String> tags = (List<String>) analysis.getOrDefault("tags", List.of());
            issue.setAiTags(String.join(",", tags));
            issue.setAiConfidence(0.85f);
            issueRepo.save(issue);

            analysis.put("issueId", issueId);
            return analysis;
        } catch (Exception e) {
            log.warn("AI classify parse error: {}", result, e);
            return Map.of("error", "parse failed", "raw", result);
        }
    }

    /** 相似Issue检索 */
    public List<Map<String, Object>> findSimilarIssues(Long issueId) {
        Issue current = issueRepo.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue不存在"));

        List<Issue> candidates = issueRepo.findByProjectId(current.getProjectId())
                .stream().filter(i -> !i.getId().equals(issueId)).limit(10).toList();
        if (candidates.isEmpty()) return List.of();

        StringBuilder sb = new StringBuilder();
        sb.append("当前Issue: ").append(current.getTitle()).append("\n");
        sb.append("候选:\n");
        for (int i = 0; i < candidates.size(); i++) {
            sb.append("[").append(i + 1).append("] ").append(candidates.get(i).getTitle()).append("\n");
        }
        sb.append("返回相似Issue的JSON数组(只返回JSON): [{\"candidateIndex\":1,\"similarity\":\"HIGH\",\"reason\":\"原因\"}] 没有则返回[]");

        String result = callDeepSeek(sb.toString());
        try {
            String json = extractJson(result);
            List<Map<String, Object>> matches = objectMapper.readValue(json,
                    new TypeReference<List<Map<String, Object>>>() {});
            return matches.stream()
                    .filter(m -> m.containsKey("candidateIndex"))
                    .map(m -> {
                        int idx = ((Number) m.get("candidateIndex")).intValue() - 1;
                        Map<String, Object> ret = new LinkedHashMap<>();
                        if (idx >= 0 && idx < candidates.size()) {
                            ret.put("issueId", candidates.get(idx).getId());
                            ret.put("title", candidates.get(idx).getTitle());
                            ret.put("status", candidates.get(idx).getStatus());
                        }
                        ret.put("similarity", m.getOrDefault("similarity", "LOW"));
                        ret.put("reason", m.getOrDefault("reason", ""));
                        return ret;
                    }).filter(m -> m.containsKey("issueId")).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    /** 每日站报 */
    public Map<String, Object> generateStandup(Long projectId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("项目不存在"));
        List<Issue> issues = issueRepo.findByProjectId(projectId);
        long done = issues.stream().filter(i -> "DONE".equals(i.getStatus())).count();
        long progress = issues.stream().filter(i -> "IN_PROGRESS".equals(i.getStatus())).count();
        long todo = issues.stream().filter(i -> "TODO".equals(i.getStatus())).count();

        String issueSummary = issues.stream()
                .map(i -> String.format("- [%s][%s] %s", i.getType(), i.getStatus(), i.getTitle()))
                .collect(Collectors.joining("\n"));

        String prompt = String.format("""
            你是%s的Scrum Master。生成今日站报，只返回JSON:
            {"summary":"2-3句话","highlights":["亮点"],"blockers":["阻塞"],"tomorrowPlan":["计划"],"metrics":{"done":%d,"total":%d,"inProgress":%d,"todo":%d}}
            Issues:
            %s""",
            project.getName(), done, issues.size(), progress, todo,
            issueSummary.isEmpty() ? "无" : issueSummary);

        String result = callDeepSeek(prompt);
        try {
            String json = extractJson(result);
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Map.of("summary", String.format("共%d个Issue，完成%d，进行中%d，待处理%d",
                    issues.size(), done, progress, todo),
                    "highlights", List.of(), "blockers", List.of(),
                    "metrics", Map.of("done", done, "total", issues.size(), "inProgress", progress, "todo", todo));
        }
    }

    /** 直接调用 DeepSeek API */
    private String callDeepSeek(String content) {
        Map<String, Object> body = Map.of(
            "model", MODEL,
            "messages", List.of(Map.of("role", "user", "content", content)),
            "temperature", 0.7,
            "max_tokens", 1000
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        try {
            String reqJson = objectMapper.writeValueAsString(body);
            HttpEntity<String> entity = new HttpEntity<>(reqJson, headers);
            ResponseEntity<String> resp = restTemplate.postForEntity(apiUrl, entity, String.class);

            // 解析 DeepSeek 响应（OpenAI格式）
            Map<String, Object> respMap = objectMapper.readValue(resp.getBody(),
                    new TypeReference<Map<String, Object>>() {});
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) respMap.get("choices");
            if (choices != null && !choices.isEmpty()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            }
            return "";
        } catch (Exception e) {
            log.error("DeepSeek API error", e);
            throw new RuntimeException("AI调用失败: " + e.getMessage());
        }
    }

    private String extractJson(String raw) {
        raw = raw.trim();
        if (raw.startsWith("```")) {
            int start = raw.indexOf("\n");
            int end = raw.lastIndexOf("```");
            if (start > 0 && end > start) raw = raw.substring(start, end).trim();
        }
        int jsonStart = -1;
        for (int i = 0; i < raw.length(); i++) {
            if (raw.charAt(i) == '[' || raw.charAt(i) == '{') { jsonStart = i; break; }
        }
        return jsonStart > 0 ? raw.substring(jsonStart) : raw;
    }
}
