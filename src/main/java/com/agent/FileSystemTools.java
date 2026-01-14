package com.agent;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemTools {

    @Tool("writes text to a file")
public void writeToFile(
    @P("The exact name of the file to create") String fileName, 
    @P("The content to write inside the file") String content
    ) {
        try {
            // This will save the file to your D drive
            Path path = Path.of("D:/AI/workspace/" + fileName);
            Files.writeString(path, content);
            System.out.println(">>> SUCCESS: Created file at " + path.toAbsolutePath());
        } catch (Exception e) {
            System.out.println(">>> ERROR: Could not write file: " + e.getMessage());
        }
    }
}