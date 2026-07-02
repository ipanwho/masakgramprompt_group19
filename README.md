**GROUP 19**

# MasakGramPrompt: Nutritional LLM Analysis Service

A comprehensive distributed system for evaluating Large Language Models (LLMs) and Prompt Engineering techniques on code-switched (Malay-English) Instagram cooking reels.

## Overview

MasakGramPrompt is designed to automate the extraction and analysis of nutritional information (ingredients, quantities, calories) from unstructured, code-switched cooking recipe transcripts. It compares the extracted results from various LLMs (like Llama 3.2) against human-annotated ground truth data.

## Tech Stack

* **Backend:** Java 21, Spring Boot 3.2.4, Hibernate/JPA
* **Database:** MySQL (Cloud-hosted via Railway)
* **LLM Engine:** Local Ollama integration
* **Frontend:** Vanilla HTML, CSS (Modern Glassmorphism Design), JavaScript

## Features

* **Automated Data Ingestion:** Scan and automatically link local audio files and transcript `.txt` files to Instagram Reel IDs.
* **Batch Processing Pipeline:** Execute a single experiment run across the entire database of transcripts in one click.
* **Prompt Engineering Engine:** Modular support for different prompt techniques (Zero-shot, Few-shot, etc.).
* **Interactive Dashboard:** 
  * Select specific reels to view their code-switched highlighted transcripts.
  * Compare Human Annotated (Baseline) ground truth data side-by-side with LLM extracted results.
  * Monitor real-time status of batch processing.

## Project Structure

* `edu.utem.ftmk.llm.controller`
  * `SetupController`: Handles initial data scanning, parsing local directories, and populating the database.
  * `LLMController`: Manages the LLM batch analysis pipeline and serves data to the frontend dashboard.
* `edu.utem.ftmk.llm.entity` / `repository`: JPA Entities mapping to the 11 core database tables (Reel, Transcript, AudioFile, Experiment, NutritionResult, IngredientResult, etc.).
* `edu.utem.ftmk.llm.service`: Contains business logic for interacting with Ollama (`LLMService`) and building prompts (`PromptEngineService`).
* `src/main/resources/static`: Contains the Dashboard UI (`index.html`, `app.js`, `styles.css`).

## Getting Started

### Prerequisites

1. Java 21 installed.
2. Maven installed.
3. Ollama running locally (with the required models downloaded, e.g., `llama3.2`).
4. Physical transcript and audio files located in the `D:\DAD_Project\nutritional-llm-service\transcriptions` and `audio` folders.

### Running the Application

1. Start the Spring Boot application by running the `MasakGramPrompt.java` main class.
2. The application will connect to the remote Railway MySQL database.
3. Access the Dashboard UI in your browser at: `http://localhost:8080/`

### Using the Pipeline

1. **Setup:** If it is a fresh database, visit `/api/setup/scan-reels` and `/api/setup/scan-audio` to ingest your local files.
2. **Dashboard:** Go to the dashboard, select a reel, choose your LLM Model and Prompt Technique.
3. **Analyze:** Click **Run Experiment** to execute the batch processing pipeline across all transcripts. The system will save the extracted `NutritionResult` and `IngredientResult`s to the database automatically.
