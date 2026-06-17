package io.github.atsin6.codereviewer.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.atsin6.codereviewer.model.dto.request.ReviewRequest;
import io.github.atsin6.codereviewer.model.dto.response.ReviewResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class CodeReviewService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    public CodeReviewService(WebClient webClient) {
        this.webClient = webClient;
        this.objectMapper = new ObjectMapper();
    }

    public ReviewResponse reviewCode(ReviewRequest request) {
        String prompt = buildPrompt(request.getLanguage(), request.getCode());

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        String url = String.format("/v1beta/models/%s:generateContent?key=%s", model, apiKey);

        try {
            String responseStr = webClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (responseStr == null) {
                throw new RuntimeException("Received empty response from Gemini API");
            }
            
            JsonNode responseNode = objectMapper.readTree(responseStr);

            String aiText = responseNode
                    .path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText();

            return parseResponse(aiText);
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            System.err.println("Gemini API Error Status: " + e.getStatusCode());
            System.err.println("Gemini API Error Body: " + e.getResponseBodyAsString());
            throw new RuntimeException("Failed to generate review: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate review", e);
        }
    }

    private String buildPrompt(String language, String code) {
        return "You are a senior software engineer.\n" +
                "Review the following " + language + " code.\n" +
                "Return ONLY valid JSON with no extra text, no markdown, no code fences:\n" +
                "{\n" +
                "  \"bugs\": \"\",\n" +
                "  \"performance\": \"\",\n" +
                "  \"bestPractices\": \"\",\n" +
                "  \"improvedCode\": \"\"\n" +
                "}\n" +
                "Code:\n" +
                code;
    }

    private ReviewResponse parseResponse(String aiText) {
        try {
            String jsonStr = aiText.trim();
            if (jsonStr.startsWith("```json")) {
                jsonStr = jsonStr.substring(7);
            } else if (jsonStr.startsWith("```")) {
                jsonStr = jsonStr.substring(3);
            }
            if (jsonStr.endsWith("```")) {
                jsonStr = jsonStr.substring(0, jsonStr.length() - 3);
            }
            jsonStr = jsonStr.trim();
            return objectMapper.readValue(jsonStr, ReviewResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }
}
