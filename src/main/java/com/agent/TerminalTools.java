package com.agent;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File; // Needed for the directory setting

public class TerminalTools {

    @Tool("Executes a system command in the terminal and returns the output")
    public String executeCommand(@P("The command to run, e.g., 'python hello.py'") String command) {
        try {
            System.out.println(">>> AGENT ACTION: Executing command: " + command);
            
            // Using ProcessBuilder to run the command via cmd.exe
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            
            // Set the working directory to your AI workspace
            // This ensures Python finds the files the agent just created
            processBuilder.directory(new File("D:/AI/workspace/")); 
            
            // Merge error messages into the standard output so the Agent can see them
            processBuilder.redirectErrorStream(true); 
            
            Process process = processBuilder.start();
            
            // Read the terminal output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            int exitCode = process.waitFor();
            String result = output.toString();
            
            if (result.trim().isEmpty()) {
                return "Command finished with exit code: " + exitCode;
            }
            return result;
        } catch (Exception e) {
            return "Error executing command: " + e.getMessage();
        }
    }
}