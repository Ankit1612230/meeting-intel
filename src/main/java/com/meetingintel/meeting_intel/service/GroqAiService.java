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

            String prompt = "You are a meeting analyst. Analyze the transcript below and extract structured information.\\n\\n"
                    + "STRICT RULES:\\n"
                    + "1. Return ONLY raw JSON - no markdown, no backticks, no explanation\\n"
                    + "2. owner field must contain the first name of the person responsible - NEVER leave it empty\\n"
                    + "3. If owner name is mentioned in the transcript, extract it\\n"
                    + "4. dueDate must be YYYY-MM-DD format only - if not mentioned use null\\n"
                    + "5. summary must be 2-3 sentences\\n\\n"
                    + "Return exactly this JSON structure:\\n"
                    + "{\\\"summary\\\": \\\"string\\\","
                    + "\\\"decisions\\\": [\\\"string\\\"],"
                    + "\\\"actionItems\\\": [{\\\"task\\\": \\\"string\\\", \\\"owner\\\": \\\"first name only\\\", \\\"dueDate\\\": \\\"YYYY-MM-DD or null\\\"}]}\\n\\n"
                    + "Meeting Transcript:\\n" + escapedTranscript;

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