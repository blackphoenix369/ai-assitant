# Autonomous Java AI Agent (llama.cpp + LangChain4j)

A powerful, local-first AI agent built with **Java 25**, **LangChain4j**, and **llama.cpp**. This agent can reason through complex logic puzzles, search the internet using Tavily, and perform file system operations—all running on your local machine.

## 🚀 Features
- **Local Inference:** Connects to `llama-server.exe` (OpenAI-compatible API).
- **Internet Access:** Integrated with Tavily Web Search for real-time data.
- **Autonomous Reasoning:** Solves multi-step logistics and scheduling problems.
- **Persistent Memory:** Uses `MessageWindowChatMemory` to remember context.
- **Tool Use:** Capable of writing files and executing system commands.

---

## 🛠️ Prerequisites

### 1. LLM Backend (llama.cpp)
You must have a compiled version of `llama.cpp` and a GGUF model file.
- **Project:** [llama.cpp](https://github.com/ggerganov/llama.cpp)
- **Recommended Model:** Llama-3-8B-Instruct (GGUF format)

### 2. Java Environment
- **JDK:** Version 25 (installed at `D:\Java\jdk-25`)
- **Build Tool:** Maven

---

## 💻 Setup & Execution

### Step 1: Start the Local LLM Server
Open a terminal and run the `llama-server`. 
*Note: Use `-ngl 0` for CPU-only or `-ngl 99` if you have NVIDIA drivers installed.*

```cmd
llama-server.exe -m "D:\Path\To\Your_Model.gguf" -c 4096 -b 512 --threads 8 --host 127.0.0.1 --port 8080

Configure API Keys
The agent uses Tavily for web searches. Ensure your API key is updated in App.java :
.apiKey("your-tavily-key-here")

Run the Agent
Compile and run the Java application:
mvn clean compile exec:java -Dexec.mainClass="com.agent.App"

Project Structure
App.java: The main entry point and AI Service definition.

SearchTool.java: Custom tool for web search integration.

FileSystemTools.java: Tools for writing/reading files (e.g., schedule.txt).

TerminalTools.java: Tools for executing local shell commands.
