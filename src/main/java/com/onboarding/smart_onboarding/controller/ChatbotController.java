package com.onboarding.smart_onboarding.controller;

import com.onboarding.smart_onboarding.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatbotController {

    @Autowired
    private AIService aiService;

    @PostMapping
    public String chat(@RequestBody String question) {
        return aiService.answerHRQuestion(question);
    }
}