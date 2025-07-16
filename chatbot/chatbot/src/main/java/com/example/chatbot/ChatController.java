package com.example.chatbot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class ChatController {

    @Value("${groq.api.key}")
    private String apiKey;

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) {
        String userMessage = request.getMessage();

        // ✅ Use Groq endpoint
        String url = "https://api.groq.com/openai/v1/chat/completions";
        RestTemplate restTemplate = new RestTemplate();

        // ✅ Use Groq-supported model
        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama3-70b-8192"); // You can also use mixtral-8x7b-32768
        body.put("messages", List.of(Map.of("role", "user", "content", userMessage)));
        body.put("max_tokens", 100);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ✅ Authorization header (Groq key)
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Map responseBody = response.getBody();
            List choices = (List) responseBody.get("choices");
            Map choice = (Map) choices.get(0);
            Map message = (Map) choice.get("message");
            return ResponseEntity.ok((String) message.get("content"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("⚠️ Error: " + e.getMessage());
        }
    }
}
