package com.onboarding.smart_onboarding.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class AIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateChecklist(String jobRole, String department) {
        String prompt = String.format(
                "You are an HR assistant. Generate a detailed onboarding checklist for a new employee. " +
                        "Job Role: %s, Department: %s. " +
                        "Return exactly 10 tasks in this format (one per line): CATEGORY|Task Name|Description. " +
                        "Categories must be one of: IT Setup, HR Documents, Training, Team Introduction, Compliance.",
                jobRole, department
        );
        return callGemini(prompt);
    }

    public String answerHRQuestion(String question) {
        String prompt = String.format(
                "You are a helpful HR assistant for a company. Answer the following employee question " +
                        "clearly and concisely in 2-3 sentences. Question: %s", question
        );
        return callGemini(prompt);
    }

    private String callGemini(String userMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", userMessage);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(textPart));

        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(content));

        String urlWithKey = apiUrl + "?key=" + apiKey;
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            System.out.println("Calling Gemini API...");
            ResponseEntity<Map> response = restTemplate.postForEntity(urlWithKey, entity, Map.class);
            System.out.println("Gemini response status: " + response.getStatusCode());
            System.out.println("Gemini response body: " + response.getBody());

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            Map<String, Object> responseContent = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) responseContent.get("parts");
            String result = (String) parts.get(0).get("text");
            System.out.println("Gemini result: " + result);
            return result;
        } catch (Exception e) {
            System.out.println("Gemini ERROR: " + e.getMessage());
            e.printStackTrace();
            return "Sorry, I could not process your request right now. Error: " + e.getMessage();
        }
    }
}