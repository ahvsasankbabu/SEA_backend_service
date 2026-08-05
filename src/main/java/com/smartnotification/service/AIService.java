package com.smartnotification.service;
import com.smartnotification.dto.GenerateRequest;
import com.smartnotification.dto.GenerateResponse;

import org.springframework.stereotype.Service;

import com.smartnotification.client.AIClient;

@Service
public class AIService {

	private final AIClient aiClient;

	public AIService(AIClient aiClient) {
		
		this.aiClient = aiClient;
	}
	
	public String checkHealth() {
		return aiClient.checkHealth();
	}
	
	public GenerateResponse analyzeEmail(GenerateRequest request) {
	    return aiClient.analyzeEmail(request);
	}
	
	
}
