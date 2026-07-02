package edu.utem.ftmk.llm.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class PromptEngineService {

    /**
     * Reads a prompt file from the resources/prompts directory.
     */
    private String readPromptFile(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource("prompts/" + fileName);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Loads the specific user prompt technique and injects the transcript.
     */
    public String buildUserPrompt(String techniqueName, String transcript) throws IOException {
        // Build the file name, e.g., "zero_shot_user.txt"
        String fileName = techniqueName + "_user.txt";
        
        // Read the raw text from the file
        String rawPrompt = readPromptFile(fileName);
        
        // Replace the placeholder with the actual transcript
        return rawPrompt.replace("{{TRANSCRIPT}}", transcript);
    }
    
    /**
     * Loads the system prompt for the specified technique.
     */
    public String buildSystemPrompt(String techniqueName) throws IOException {
        String fileName = techniqueName + "_system.txt";
        return readPromptFile(fileName);
    }
}