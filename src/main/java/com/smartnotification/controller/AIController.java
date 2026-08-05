package com.smartnotification.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.smartnotification.dto.GenerateRequest;
import com.smartnotification.dto.GenerateResponse;

import com.smartnotification.service.AIService;

@RestController
public class AIController {
	
	private final AIService aiService;

	public AIController(AIService aiService) {
		
		this.aiService = aiService;
	}
	
	@GetMapping("/api/ai/health")
	public String checkHealth() {
		return aiService.checkHealth();
	}
	
	@PostMapping("/api/ai/analyze")
	public GenerateResponse analyzeEmail(@RequestBody GenerateRequest request) {
	    return aiService.analyzeEmail(request);
	}
	
	

}
