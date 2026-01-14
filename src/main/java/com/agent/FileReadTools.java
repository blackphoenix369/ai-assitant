package com.agent;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReadTools {

    @Tool("Reads the content of a specific file from the workspace")
    public String readFile(@P("The name of the file to read, e.g., 'math_test.py'") String fileName) {
        try {
            // Consistency: Always use the same base path as your other tools
            Path path = Path.of("D:/AI/workspace/" + fileName);
            
            if (!Files.exists(path)) {
                return "Error: The file '" + fileName + "' does not exist in the workspace.";
            }

            // Safety Check: Don't let the AI crash your app with a giant file
            long fileSize = Files.size(path);
            if (fileSize > 1024 * 1024) { // 1 MB limit
                return "Error: File is too large to read (Limit: 1MB).";
            }

            System.out.println(">>> AGENT ACTION: Reading file contents of " + fileName);
            return Files.readString(path);
            
        } catch (Exception e) {
            return "Error while reading file: " + e.getMessage();
        }
    }
}