package com.agent;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.tavily.TavilyWebSearchEngine;
import dev.langchain4j.agent.tool.Tool;
import java.time.Duration;

public class App {

    // 1. Define the AI Service Interface
    interface Assistant {
        @dev.langchain4j.service.SystemMessage("""
            You are a Software Engineer with Internet Access.
            - To get real-time info, you MUST call the 'search' tool.
            - After searching, use 'writeToFile' to save data.
            - Finally, use 'executeCommand' to run scripts.
            DO NOT just talk about steps. EXECUTE them one by one.
            """)
        String chat(String message);
    }

    public static void main(String[] args) {
        
        // 2. Setup the Chat Model (Fixed: Declared only ONCE)
        ChatLanguageModel model = OpenAiChatModel.builder()
            .baseUrl("http://localhost:8080/v1")
            .apiKey("none")
            .timeout(Duration.ofMinutes(20)) // Long timeout for local LLM
            .maxRetries(3)                  // Retry if server hangs
            .logRequests(true)
            .logResponses(true)
            .build();

        // 3. Setup Tools (Tavily search)
        WebSearchEngine tavilyEngine = TavilyWebSearchEngine.builder()
                .apiKey("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")
                .build();
        SearchTool searchTool = new SearchTool(tavilyEngine);

        // 4. Build the Assistant (Fixed: Created globally in main)
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .tools(
                    new FileSystemTools(), 
                    new TerminalTools(), 
                    new FileReadTools(), 
                    searchTool
                )
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        System.out.println("Agent Online. Starting autonomous task...");

        // 5. Run the Task with the Hard Prompt
        try {
            String hardPrompt = 
                "SYSTEM: You are a Lead Logistics Agent for a high-security research facility. " +
                "You must solve complex scheduling problems with 100% logical accuracy.\n\n" +
                "SCENARIO:\n" +
                "Teams Alpha, Bravo, and Charlie each require a 1-hour session today (09:00 to 13:00).\n\n" +
                "CONSTRAINTS:\n" +
                "1. DEPENDENCIES: Team Bravo must start AFTER Alpha finishes.\n" +
                "2. TIME LIMIT: Team Bravo cannot start later than 11:30.\n" +
                "3. PRECISION: Team Charlie must start exactly 30 minutes after Bravo finishes.\n" +
                "4. LOCKOUT: Team Charlie cannot be in the building during 'Cooling System' (10:30 - 11:30).\n" +
                "5. SECURITY: 15-minute Security Sweep between every session.\n" +
                "6. POWER: Only one team inside at a time.\n\n" +
                "USER: Generate the most efficient schedule possible.";

            // Fixed: This now resolves to the assistant created above
            String response = assistant.chat(hardPrompt);
            
            System.out.println("\n=== FINAL RESPONSE ===\n" + response);
        } catch (Exception e) {
            System.err.println("Execution Error: " + e.getMessage());
            e.printStackTrace(); // Useful for debugging the exact timeout line
        }
    }

    // 6. Tool Class Definition
    static class SearchTool {
        private final WebSearchEngine searchEngine;

        SearchTool(WebSearchEngine searchEngine) {
            this.searchEngine = searchEngine;
        }

        @Tool("Search the internet for real-time information or news")
        public String search(String query) {
            return searchEngine.search(query).results().stream()
                    .map(result -> result.title() + ": " + result.snippet())
                    .reduce((a, b) -> a + "\n\n" + b)
                    .orElse("No results found.");
        }
    }
}
//"D:\AgenticAI\llama.cpp\build\bin\Release\llama-server.exe" --model "D:\AI\models\Qwen2.5-Coder-7B-Instruct-Q4_K_M.gguf" --port 8080