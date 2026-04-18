package com.arnab.taskqueue.infrastructure.webhook;

import com.arnab.taskqueue.domain.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class WebhookService {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    public boolean sendWebhook(Task task) {

        if (task.getWebhookUrl() == null) {
            return true; // nothing to send
        }

        try {

            String payload = buildPayload(task);

            String signature = generateHmac(task.getWebhookSecret(), payload);

            URL url = new URL(task.getWebhookUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("X-Signature", signature);
            conn.setDoOutput(true);

            conn.getOutputStream().write(payload.getBytes(StandardCharsets.UTF_8));

            int responseCode = conn.getResponseCode();

            log.info("Webhook response for task {}: {}", task.getId(), responseCode);

            return responseCode >= 200 && responseCode < 300;

        } catch (Exception e) {
            log.error("Webhook failed for task {}", task.getId(), e);
            return false;
        }
    }

    private String buildPayload(Task task) {

        return String.format(
                "{\"taskId\":\"%s\",\"status\":\"%s\",\"result\":\"%s\",\"error\":\"%s\"}",
                task.getId(),
                task.getStatus(),
                task.getResult(),
                task.getErrorMessage()
        );
    }

    private String generateHmac(String secret, String payload) throws Exception {

        if (secret == null) return "";

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        mac.init(key);

        byte[] raw = mac.doFinal(payload.getBytes());

        StringBuilder hex = new StringBuilder();
        for (byte b : raw) {
            hex.append(String.format("%02x", b));
        }

        return hex.toString();
    }
}
