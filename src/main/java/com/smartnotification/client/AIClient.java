package com.smartnotification.client;

import org.springframework.beans.factory.annotation.Value;
import com.smartnotification.dto.GenerateRequest;
import com.smartnotification.dto.GenerateResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AIClient {
	
	private final RestClient restClient;
	private final String aiServiceBaseUrl;
	private final String apiKey;

	public AIClient(
	        RestClient restClient,
	        @Value("${ai.service.base-url}") String aiServiceBaseUrl,
	        @Value("${ai.service.api-key}") String apiKey) {

	    this.restClient = restClient;
	    this.aiServiceBaseUrl = aiServiceBaseUrl;
	    this.apiKey = apiKey;
	}
	
	
	
    public String checkHealth() {

        return restClient.get()
                .uri(aiServiceBaseUrl + "/health")
                .retrieve()
                .body(String.class);

    }
    public GenerateResponse analyzeEmail(GenerateRequest request) {

        System.out.println("========== CALLING FASTAPI ==========");
        System.out.println("Sender      : " + request.getEmailSender());
        System.out.println("Subject     : " + request.getEmailSubject());
        System.out.println("Preference  : " + request.getUserPreference());

        GenerateResponse response = restClient.post()
                .uri(aiServiceBaseUrl + "/generate")
                .header("X-API-Key", apiKey)
                .body(request)
                .retrieve()
                .body(GenerateResponse.class);

        System.out.println("========== FASTAPI RETURNED ==========");
        System.out.println(response);

        return response;
    }
	

}
