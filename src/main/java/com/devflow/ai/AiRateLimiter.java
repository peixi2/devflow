package com.devflow.ai;

import com.devflow.common.TooManyRequestsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;

@Component
public class AiRateLimiter {

    private final int requestsPerMinute;
    private final Deque<Instant> requests = new ArrayDeque<>();

    public AiRateLimiter(@Value("${app.ai.rate-limit-per-minute:10}") int requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }

    public synchronized void checkAllowed() {
        if (requestsPerMinute <= 0) {
            return;
        }

        Instant cutoff = Instant.now().minus(Duration.ofMinutes(1));
        while (!requests.isEmpty() && requests.peekFirst().isBefore(cutoff)) {
            requests.removeFirst();
        }
        if (requests.size() >= requestsPerMinute) {
            throw new TooManyRequestsException("AI 请求过于频繁，请稍后再试");
        }
        requests.addLast(Instant.now());
    }
}
