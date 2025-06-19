package com.project.travel.controller.AiController;
import com.project.travel.domain.ChatRequest;
import com.project.travel.domain.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Value("${doubao.api-key}")
    private String apiKey;

    @Value("${doubao.endpoint}")
    private String doubaoEndpoint;

    @PostMapping("/send")
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody ChatRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    doubaoEndpoint,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
            Map<String, Object> body = response.getBody();
            System.out.println("Raw Doubao Response: " + body); // Log raw response

            ChatResponse chatResponse = new ChatResponse();
            if (body != null && body.containsKey("choices")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> choice = ((List<Map<String, Object>>) body.get("choices")).get(0);
                @SuppressWarnings("unchecked")
                Map<String, Object> message = (Map<String, Object>) choice.get("message");
                chatResponse.setContent((String) message.get("content"));
            } else {
                chatResponse.setContent("No content in response");
            }
            return ResponseEntity.ok(chatResponse);
        } catch (Exception e) {
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setContent("Error: Failed to connect to Doubao API - " + e.getMessage());
            System.out.println("Error: " + e.getMessage()); // Log error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}