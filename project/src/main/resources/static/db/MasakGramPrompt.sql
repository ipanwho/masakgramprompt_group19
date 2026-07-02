-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: switchback.proxy.rlwy.net    Database: masakgramprompt
-- ------------------------------------------------------
-- Server version	9.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `audio_file`
--

DROP TABLE IF EXISTS `audio_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audio_file` (
  `audio_id` int NOT NULL AUTO_INCREMENT,
  `reel_id` int NOT NULL COMMENT 'FK → reel.reel_id',
  `file_name` varchar(200) NOT NULL COMMENT 'Audio file name e.g. DV7uZzBE47j.mp3',
  `file_path` varchar(500) NOT NULL COMMENT 'Full path to the audio file on disk',
  `file_created_at` timestamp NULL DEFAULT NULL COMMENT 'File creation timestamp from filesystem metadata',
  `file_size_bytes` bigint DEFAULT NULL COMMENT 'Audio file size in bytes',
  `file_format` varchar(10) NOT NULL COMMENT 'Audio format e.g. mp3',
  `reel_audio_consistent` tinyint(1) DEFAULT NULL COMMENT 'True if audio matches the Instagram Reel content',
  `verified_by_matric` varchar(20) DEFAULT NULL COMMENT 'Matric number of the audio verifier',
  `verified_by_name` varchar(100) DEFAULT NULL COMMENT 'Name of the audio verifier',
  `verified_at` timestamp NULL DEFAULT NULL COMMENT 'Timestamp when audio verification was completed',
  PRIMARY KEY (`audio_id`),
  KEY `fk_audio_reel` (`reel_id`),
  CONSTRAINT `fk_audio_reel` FOREIGN KEY (`reel_id`) REFERENCES `reel` (`reel_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Audio files extracted from each reel with verification records';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audio_file`
--

LOCK TABLES `audio_file` WRITE;
/*!40000 ALTER TABLE `audio_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `audio_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `experiment`
--

DROP TABLE IF EXISTS `experiment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `experiment` (
  `experiment_id` int NOT NULL AUTO_INCREMENT,
  `transcript_id` int NOT NULL COMMENT 'FK → transcript.transcript_id',
  `model_id` int NOT NULL COMMENT 'FK → llm_model.model_id',
  `technique_id` int NOT NULL COMMENT 'FK → prompt_technique.technique_id',
  `rag_enabled` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'False for Phase 1 (prompt engineering only); True for Phase 2 (RAG)',
  `status` varchar(20) NOT NULL DEFAULT 'pending' COMMENT 'Execution status: pending, running, completed, failed',
  `executed_at` timestamp NULL DEFAULT NULL COMMENT 'Timestamp when the experiment was executed',
  PRIMARY KEY (`experiment_id`),
  KEY `fk_experiment_transcript` (`transcript_id`),
  KEY `fk_experiment_model` (`model_id`),
  KEY `fk_experiment_technique` (`technique_id`),
  CONSTRAINT `fk_experiment_model` FOREIGN KEY (`model_id`) REFERENCES `llm_model` (`model_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_experiment_technique` FOREIGN KEY (`technique_id`) REFERENCES `prompt_technique` (`technique_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_experiment_transcript` FOREIGN KEY (`transcript_id`) REFERENCES `transcript` (`transcript_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Experimental runs: each row = one transcript × LLM × prompt technique combination';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `experiment`
--

LOCK TABLES `experiment` WRITE;
/*!40000 ALTER TABLE `experiment` DISABLE KEYS */;
/*!40000 ALTER TABLE `experiment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ground_truth_ingredient`
--

DROP TABLE IF EXISTS `ground_truth_ingredient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ground_truth_ingredient` (
  `gt_ingredient_id` int NOT NULL AUTO_INCREMENT,
  `gt_reel_id` int NOT NULL COMMENT 'FK → ground_truth_reel.gt_reel_id',
  `name_original` varchar(200) NOT NULL COMMENT 'Ingredient name exactly as spoken in the transcript (Identified Name)',
  `language_mentioned` varchar(5) NOT NULL COMMENT 'Language of the ingredient name: MY, EN, or OT',
  `name_en` varchar(200) DEFAULT NULL COMMENT 'English equivalent of the ingredient name (English Name)',
  `quantity_expression` varchar(200) DEFAULT NULL COMMENT 'Faithful transcription of how the quantity was expressed e.g. dua sudu besar, sikit je',
  `quantity_category` varchar(50) DEFAULT NULL COMMENT 'Category from controlled vocabulary in Appendix A',
  `quantity_unit_culinary` varchar(100) DEFAULT NULL COMMENT 'Standardised culinary unit from controlled vocabulary in Appendix B; NULL for vague/taste-based/not_mentioned categories',
  `quantity_value_culinary` float DEFAULT NULL COMMENT 'Numeric count or measurement extracted from transcript e.g. 0.5, 1.5, 2.5',
  `estimated_weight_g` float DEFAULT NULL COMMENT 'Ingredient weight in grams derived via Layer 2 unit conversion',
  `calories` float DEFAULT NULL COMMENT 'Ground truth caloric value in kilocalories (kcal)',
  `total_fat_g` float DEFAULT NULL COMMENT 'Ground truth total fat content in grams',
  `saturated_fat_g` float DEFAULT NULL COMMENT 'Ground truth saturated fat content in grams',
  `cholesterol_mg` float DEFAULT NULL COMMENT 'Ground truth cholesterol content in milligrams',
  `sodium_mg` float DEFAULT NULL COMMENT 'Ground truth sodium content in milligrams',
  `total_carbohydrate_g` float DEFAULT NULL COMMENT 'Ground truth total carbohydrate content in grams',
  `dietary_fiber_g` float DEFAULT NULL COMMENT 'Ground truth dietary fiber content in grams',
  `total_sugars_g` float DEFAULT NULL COMMENT 'Ground truth total sugars content in grams',
  `protein_g` float DEFAULT NULL COMMENT 'Ground truth protein content in grams',
  `vitamin_d_mcg` float DEFAULT NULL COMMENT 'Ground truth vitamin D content in micrograms',
  `calcium_mg` float DEFAULT NULL COMMENT 'Ground truth calcium content in milligrams',
  `iron_mg` float DEFAULT NULL COMMENT 'Ground truth iron content in milligrams',
  `potassium_mg` float DEFAULT NULL COMMENT 'Ground truth potassium content in milligrams',
  `annotation_layer` varchar(10) NOT NULL COMMENT 'layer1 = faithful transcription; layer2 = standardised unit conversion',
  `annotator_matric` varchar(20) NOT NULL COMMENT 'Matric number of the annotator who completed this record',
  `annotator_name` varchar(100) NOT NULL COMMENT 'Full name of the annotator who completed this record',
  `annotated_at` timestamp NULL DEFAULT NULL COMMENT 'Timestamp recording when the annotation was submitted',
  PRIMARY KEY (`gt_ingredient_id`),
  KEY `fk_gt_ingredient_reel` (`gt_reel_id`),
  CONSTRAINT `fk_gt_ingredient_reel` FOREIGN KEY (`gt_reel_id`) REFERENCES `ground_truth_reel` (`gt_reel_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Ingredient-level ground truth annotations with nutritional values';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ground_truth_ingredient`
--

LOCK TABLES `ground_truth_ingredient` WRITE;
/*!40000 ALTER TABLE `ground_truth_ingredient` DISABLE KEYS */;
/*!40000 ALTER TABLE `ground_truth_ingredient` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ground_truth_reel`
--

DROP TABLE IF EXISTS `ground_truth_reel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ground_truth_reel` (
  `gt_reel_id` int NOT NULL AUTO_INCREMENT,
  `transcript_id` int NOT NULL COMMENT 'FK → transcript.transcript_id',
  `annotator_matric` varchar(20) NOT NULL COMMENT 'Matric number of the annotator',
  `annotator_name` varchar(100) NOT NULL COMMENT 'Name of the annotator',
  `annotated_at` timestamp NULL DEFAULT NULL COMMENT 'Timestamp when annotation was completed',
  PRIMARY KEY (`gt_reel_id`),
  KEY `fk_gt_reel_transcript` (`transcript_id`),
  CONSTRAINT `fk_gt_reel_transcript` FOREIGN KEY (`transcript_id`) REFERENCES `transcript` (`transcript_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Reel-level ground truth annotations by human annotators';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ground_truth_reel`
--

LOCK TABLES `ground_truth_reel` WRITE;
/*!40000 ALTER TABLE `ground_truth_reel` DISABLE KEYS */;
/*!40000 ALTER TABLE `ground_truth_reel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `influencer`
--

DROP TABLE IF EXISTS `influencer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `influencer` (
  `influencer_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT 'Full name of the influencer',
  `instagram_account` varchar(100) NOT NULL COMMENT 'Instagram handle e.g. khairulaming',
  `instagram_url` varchar(500) NOT NULL COMMENT 'Full URL to the Instagram profile',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Record creation timestamp',
  PRIMARY KEY (`influencer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Profile of the selected Malaysian gastronomy influencer';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `influencer`
--

LOCK TABLES `influencer` WRITE;
/*!40000 ALTER TABLE `influencer` DISABLE KEYS */;
INSERT INTO `influencer` VALUES (1,'Hadi Salleh','hadi.sallehh','https://www.instagram.com/hadi.sallehh/','2026-06-24 14:23:47');
/*!40000 ALTER TABLE `influencer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingredient_result`
--

DROP TABLE IF EXISTS `ingredient_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ingredient_result` (
  `ingredient_id` int NOT NULL AUTO_INCREMENT,
  `result_id` int NOT NULL COMMENT 'FK → nutrition_result.result_id',
  `name_original` varchar(200) DEFAULT NULL COMMENT 'Ingredient name as spoken in the transcript',
  `name_en` varchar(200) DEFAULT NULL COMMENT 'English translation of the ingredient name',
  `quantity_value` float DEFAULT NULL COMMENT 'Numeric quantity as extracted by LLM',
  `unit_original` varchar(100) DEFAULT NULL COMMENT 'Unit as extracted by LLM e.g. sudu besar',
  `unit_en` varchar(100) DEFAULT NULL COMMENT 'English translation of the unit',
  `estimated_weight_g` float DEFAULT NULL COMMENT 'Estimated weight in grams as computed by LLM',
  `calories` float DEFAULT NULL COMMENT 'Estimated calories in kilocalories (kcal)',
  `total_fat_g` float DEFAULT NULL COMMENT 'Estimated total fat in grams',
  `saturated_fat_g` float DEFAULT NULL COMMENT 'Estimated saturated fat in grams',
  `cholesterol_mg` float DEFAULT NULL COMMENT 'Estimated cholesterol in milligrams',
  `sodium_mg` float DEFAULT NULL COMMENT 'Estimated sodium in milligrams',
  `total_carbohydrate_g` float DEFAULT NULL COMMENT 'Estimated total carbohydrate in grams',
  `dietary_fiber_g` float DEFAULT NULL COMMENT 'Estimated dietary fiber in grams',
  `total_sugars_g` float DEFAULT NULL COMMENT 'Estimated total sugars in grams',
  `protein_g` float DEFAULT NULL COMMENT 'Estimated protein in grams',
  `vitamin_d_mcg` float DEFAULT NULL COMMENT 'Estimated vitamin D in micrograms',
  `calcium_mg` float DEFAULT NULL COMMENT 'Estimated calcium in milligrams',
  `iron_mg` float DEFAULT NULL COMMENT 'Estimated iron in milligrams',
  `potassium_mg` float DEFAULT NULL COMMENT 'Estimated potassium in milligrams',
  PRIMARY KEY (`ingredient_id`),
  KEY `fk_ingredient_nutrition` (`result_id`),
  CONSTRAINT `fk_ingredient_nutrition` FOREIGN KEY (`result_id`) REFERENCES `nutrition_result` (`result_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Ingredient-level nutritional values extracted by the LLM per experiment run';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ingredient_result`
--

LOCK TABLES `ingredient_result` WRITE;
/*!40000 ALTER TABLE `ingredient_result` DISABLE KEYS */;
/*!40000 ALTER TABLE `ingredient_result` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `llm_model`
--

DROP TABLE IF EXISTS `llm_model`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `llm_model` (
  `model_id` int NOT NULL AUTO_INCREMENT,
  `model_name` varchar(100) NOT NULL COMMENT 'Display name e.g. Llama 3.1 8B Instruct',
  `model_tag` varchar(100) NOT NULL COMMENT 'Ollama model tag e.g. llama3.1:8b',
  `provider` varchar(100) NOT NULL COMMENT 'Model provider e.g. Meta, Mistral AI, Alibaba, Saama AI',
  `description` text COMMENT 'Brief description of the model and its relevance to the study',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Record creation timestamp',
  PRIMARY KEY (`model_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Reference table for the four LLMs evaluated in the study';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `llm_model`
--

LOCK TABLES `llm_model` WRITE;
/*!40000 ALTER TABLE `llm_model` DISABLE KEYS */;
INSERT INTO `llm_model` VALUES (1,'Llama 3.2 3B Instruct','llama3.2:3b','Meta','Meta Llama 3.2 3B instruction-tuned model. Selected as a compact general-purpose baseline that runs comfortably on medium-specification machines while retaining solid multilingual comprehension for EN-MS code-switched nutritional extraction.','2026-06-24 12:26:47'),(2,'Phi-4-mini 3.8B Instruct','phi4-mini','Microsoft','Microsoft Phi-4-mini 3.8B instruction-tuned model. Selected as an efficiency benchmark, offering strong reasoning density per parameter at a small footprint suitable for CPU-based batch processing of cooking transcripts.','2026-06-24 12:26:47'),(3,'Qwen 2.5 3B Instruct','qwen2.5:3b','Alibaba','Alibaba Qwen 2.5 3B instruction-tuned model. Selected for its broad multilingual coverage including Malay and English, providing a general multilingual reference point for code-switched gastronomy transcript analysis.','2026-06-24 12:26:47'),(4,'Gemma-SEA-LION v4 4B','aisingapore/Gemma-SEA-LION-v4-4B-VL','AI Singapore','AI Singapore Gemma-SEA-LION v4 4B model, post-trained on Southeast Asian languages including Malay on a Gemma 3 base. Selected to test whether regional language adaptation improves extraction accuracy on EN-MS code-switched cooking transcripts compared with general multilingual models.','2026-06-24 12:26:47'),(5,'MedGemma 4B','medgemma:4b','Google','Google MedGemma 4B model adapted for medical and biomedical text on a Gemma 3 base. Selected to evaluate whether domain-specific pretraining improves nutritional information extraction accuracy relative to general-purpose instruction-tuned models.','2026-06-24 12:26:47');
/*!40000 ALTER TABLE `llm_model` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nutrition_result`
--

DROP TABLE IF EXISTS `nutrition_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nutrition_result` (
  `result_id` int NOT NULL AUTO_INCREMENT,
  `experiment_id` int NOT NULL COMMENT 'FK → experiment.experiment_id',
  `recipe_name` varchar(200) DEFAULT NULL COMMENT 'Recipe name extracted by the LLM',
  `servings_estimated` int DEFAULT NULL COMMENT 'Number of servings estimated by the LLM',
  `serving_calories` float DEFAULT NULL COMMENT 'Calories per serving (kcal)',
  `serving_total_fat_g` float DEFAULT NULL COMMENT 'Total fat per serving in grams',
  `serving_saturated_fat_g` float DEFAULT NULL COMMENT 'Saturated fat per serving in grams',
  `serving_cholesterol_mg` float DEFAULT NULL COMMENT 'Cholesterol per serving in milligrams',
  `serving_sodium_mg` float DEFAULT NULL COMMENT 'Sodium per serving in milligrams',
  `serving_carbohydrate_g` float DEFAULT NULL COMMENT 'Total carbohydrate per serving in grams',
  `serving_fiber_g` float DEFAULT NULL COMMENT 'Dietary fiber per serving in grams',
  `serving_sugars_g` float DEFAULT NULL COMMENT 'Total sugars per serving in grams',
  `serving_protein_g` float DEFAULT NULL COMMENT 'Protein per serving in grams',
  `serving_vitamin_d_mcg` float DEFAULT NULL COMMENT 'Vitamin D per serving in micrograms',
  `serving_calcium_mg` float DEFAULT NULL COMMENT 'Calcium per serving in milligrams',
  `serving_iron_mg` float DEFAULT NULL COMMENT 'Iron per serving in milligrams',
  `serving_potassium_mg` float DEFAULT NULL COMMENT 'Potassium per serving in milligrams',
  `total_calories` float DEFAULT NULL COMMENT 'Total calories for the full recipe (kcal)',
  `total_fat_g` float DEFAULT NULL COMMENT 'Total fat for the full recipe in grams',
  `total_saturated_fat_g` float DEFAULT NULL COMMENT 'Total saturated fat for the full recipe in grams',
  `total_cholesterol_mg` float DEFAULT NULL COMMENT 'Total cholesterol for the full recipe in milligrams',
  `total_sodium_mg` float DEFAULT NULL COMMENT 'Total sodium for the full recipe in milligrams',
  `total_carbohydrate_g` float DEFAULT NULL COMMENT 'Total carbohydrate for the full recipe in grams',
  `total_fiber_g` float DEFAULT NULL COMMENT 'Total dietary fiber for the full recipe in grams',
  `total_sugars_g` float DEFAULT NULL COMMENT 'Total sugars for the full recipe in grams',
  `total_protein_g` float DEFAULT NULL COMMENT 'Total protein for the full recipe in grams',
  `total_vitamin_d_mcg` float DEFAULT NULL COMMENT 'Total vitamin D for the full recipe in micrograms',
  `total_calcium_mg` float DEFAULT NULL COMMENT 'Total calcium for the full recipe in milligrams',
  `total_iron_mg` float DEFAULT NULL COMMENT 'Total iron for the full recipe in milligrams',
  `total_potassium_mg` float DEFAULT NULL COMMENT 'Total potassium for the full recipe in milligrams',
  `raw_json_output` text COMMENT 'Original raw JSON response from the LLM for debugging',
  `json_valid` tinyint(1) DEFAULT NULL COMMENT 'True if the LLM output was valid parseable JSON',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Record creation timestamp',
  PRIMARY KEY (`result_id`),
  KEY `fk_nutrition_experiment` (`experiment_id`),
  CONSTRAINT `fk_nutrition_experiment` FOREIGN KEY (`experiment_id`) REFERENCES `experiment` (`experiment_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='LLM-generated structured nutritional output per experiment run';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nutrition_result`
--

LOCK TABLES `nutrition_result` WRITE;
/*!40000 ALTER TABLE `nutrition_result` DISABLE KEYS */;
/*!40000 ALTER TABLE `nutrition_result` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prompt_technique`
--

DROP TABLE IF EXISTS `prompt_technique`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prompt_technique` (
  `technique_id` int NOT NULL AUTO_INCREMENT,
  `technique_name` varchar(50) NOT NULL COMMENT 'Technique name e.g. zero-shot, few-shot, chain-of-thought, structured-output',
  `system_prompt_file` varchar(500) NOT NULL COMMENT 'Relative path to system prompt file e.g. prompts/zero_shot_system.txt',
  `user_prompt_file` varchar(500) NOT NULL COMMENT 'Relative path to user prompt template file e.g. prompts/zero_shot_user.txt',
  `prompt_version` varchar(10) NOT NULL COMMENT 'Version of the prompt files e.g. 1.0',
  `description` text COMMENT 'Brief description of the technique and its characteristics',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Record creation timestamp',
  PRIMARY KEY (`technique_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Reference table for the four prompt engineering techniques';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prompt_technique`
--

LOCK TABLES `prompt_technique` WRITE;
/*!40000 ALTER TABLE `prompt_technique` DISABLE KEYS */;
INSERT INTO `prompt_technique` VALUES (1,'zero-shot','prompts/zero_shot_system.txt','prompts/zero_shot_user.txt','1.0','Zero-shot prompting provides the model with task instructions and output format requirements only, without any examples. The model relies entirely on its pretrained knowledge to extract nutritional information from EN-MS code-switched cooking transcripts.','2026-06-24 12:26:47'),(2,'few-shot','prompts/few_shot_system.txt','prompts/few_shot_user.txt','1.0','Few-shot prompting supplies the model with a small number of annotated input-output examples before the target transcript. This guides the model toward the expected JSON output structure and handling of informal Malay culinary expressions.','2026-06-24 12:26:47'),(3,'chain-of-thought','prompts/chain_of_thought_system.txt','prompts/chain_of_thought_user.txt','1.0','Chain-of-thought prompting instructs the model to reason step-by-step before producing the final JSON output. This encourages explicit intermediate reasoning over ingredient identification, quantity interpretation, and language tagging in code-switched text.','2026-06-24 12:26:47'),(4,'structured-output','prompts/structured_output_system.txt','prompts/structured_output_user.txt','1.0','Structured-output prompting enforces a strict JSON schema in the prompt instructions, constraining the model response to a predefined format. This reduces hallucination risk and improves consistency of the extracted nutritional fields across all five LLMs.','2026-06-24 12:26:47');
/*!40000 ALTER TABLE `prompt_technique` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reel`
--

DROP TABLE IF EXISTS `reel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reel` (
  `reel_id` int NOT NULL AUTO_INCREMENT,
  `influencer_id` int NOT NULL COMMENT 'FK → influencer.influencer_id',
  `reel_id_instagram` varchar(50) NOT NULL COMMENT 'Instagram reel ID extracted from URL e.g. DV7uZzBE47j',
  `reel_url` varchar(500) NOT NULL COMMENT 'Full Instagram Reel URL',
  `identified_by_matric` varchar(20) NOT NULL COMMENT 'Matric number of team member who identified the reel',
  `identified_by_name` varchar(100) NOT NULL COMMENT 'Name of team member who identified the reel',
  `identified_date` date NOT NULL COMMENT 'Date the reel was identified',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Record creation timestamp',
  PRIMARY KEY (`reel_id`),
  KEY `fk_reel_influencer` (`influencer_id`),
  CONSTRAINT `fk_reel_influencer` FOREIGN KEY (`influencer_id`) REFERENCES `influencer` (`influencer_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Instagram Reels identified during data collection';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reel`
--

LOCK TABLES `reel` WRITE;
/*!40000 ALTER TABLE `reel` DISABLE KEYS */;
/*!40000 ALTER TABLE `reel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transcript`
--

DROP TABLE IF EXISTS `transcript`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transcript` (
  `transcript_id` int NOT NULL AUTO_INCREMENT,
  `reel_id` int NOT NULL COMMENT 'FK → reel.reel_id',
  `audio_id` int NOT NULL COMMENT 'FK → audio_file.audio_id',
  `file_name` varchar(200) NOT NULL COMMENT 'Transcript file name e.g. transcription_20260406_081309.txt',
  `file_path` varchar(500) NOT NULL COMMENT 'Full path to the transcript file on disk',
  `file_created_at` timestamp NULL DEFAULT NULL COMMENT 'File creation timestamp from filesystem metadata',
  `file_size_bytes` bigint DEFAULT NULL COMMENT 'Transcript file size in bytes',
  `file_format` varchar(10) NOT NULL COMMENT 'File format e.g. txt',
  `audio_transcript_consistent` tinyint(1) DEFAULT NULL COMMENT 'True if transcript matches the audio content',
  `verified_by_matric` varchar(20) DEFAULT NULL COMMENT 'Matric number of the transcript verifier',
  `verified_by_name` varchar(100) DEFAULT NULL COMMENT 'Name of the transcript verifier',
  `verified_at` timestamp NULL DEFAULT NULL COMMENT 'Timestamp when transcript verification was completed',
  PRIMARY KEY (`transcript_id`),
  KEY `fk_transcript_reel` (`reel_id`),
  KEY `fk_transcript_audio` (`audio_id`),
  CONSTRAINT `fk_transcript_audio` FOREIGN KEY (`audio_id`) REFERENCES `audio_file` (`audio_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_transcript_reel` FOREIGN KEY (`reel_id`) REFERENCES `reel` (`reel_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Faster-Whisper transcripts with verification records';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transcript`
--

LOCK TABLES `transcript` WRITE;
/*!40000 ALTER TABLE `transcript` DISABLE KEYS */;
/*!40000 ALTER TABLE `transcript` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-25 10:43:51
