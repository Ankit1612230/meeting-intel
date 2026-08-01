package com.meetingintel.meeting_intel.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GroqAiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.model}")
    private String model;

    public GroqAiService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.groq.com")
                .build();
        this.objectMapper = objectMapper;
    }

    public String analyzeTranscript(String transcript) {
        try {
            String escapedTranscript = transcript
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");

            String prompt = "Analyze the following meeting transcript and extract:\\n"
                    + "1. A brief summary\\n"
                    + "2. Key decisions made\\n"
                    + "3. Action items with owner email (use exact email addresses from the transcript if mentioned) and due date in YYYY-MM-DD format only\\n\\n"
                    + "Return ONLY this JSON format, nothing else:\\n"
                    + "{\\\"summary\\\": \\\"...\\\", "
                    + "\\\"decisions\\\": [\\\"decision1\\\"], "
                    + "\\\"actionItems\\\": [{\\\"task\\\": \\\"...\\\", "
                    + "\\\"owner\\\": \\\"...\\\", \\\"dueDate\\\": \\\"YYYY-MM-DD\\\"}]}"
                    + "\\n\\nTranscript: " + escapedTranscript;

            String requestBody = "{"
                    + "\"model\": \"" + model + "\","
                    + "\"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}],"
                    + "\"temperature\": 0.3"
                    + "}";

            String response = webClient.post()
                    .uri("/openai/v1/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            return root.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

        }  catch (Exception e) {
        // Print full error details
        System.out.println("GROQ ERROR: " + e.getMessage());
        if (e instanceof org.springframework.web.reactive.function.client.WebClientResponseException ex) {
            System.out.println("GROQ RESPONSE BODY: " + ex.getResponseBodyAsString());
        }
        throw new RuntimeException("Failed to analyze transcript: " + e.getMessage());
    }
    }
}