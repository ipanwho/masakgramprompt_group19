package edu.utem.ftmk.llm.controller;

import edu.utem.ftmk.llm.service.LLMService;
import edu.utem.ftmk.llm.service.PromptEngineService;
import edu.utem.ftmk.llm.entity.NutritionResult;
import edu.utem.ftmk.llm.repository.NutritionResultRepository;
import edu.utem.ftmk.llm.entity.GroundTruthIngredient;
import edu.utem.ftmk.llm.repository.GroundTruthIngredientRepository;
import edu.utem.ftmk.llm.entity.Transcript;
import edu.utem.ftmk.llm.repository.TranscriptRepository;
import edu.utem.ftmk.llm.entity.Reel;
import edu.utem.ftmk.llm.repository.ReelRepository;
import edu.utem.ftmk.llm.entity.Experiment;
import edu.utem.ftmk.llm.repository.ExperimentRepository;
import edu.utem.ftmk.llm.entity.IngredientResult;
import edu.utem.ftmk.llm.repository.IngredientResultRepository;
import edu.utem.ftmk.llm.repository.AudioFileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/llm")
@CrossOrigin(origins = "*")
public class LLMController {

	@Autowired
	private LLMService llmService;

	@Autowired
	private PromptEngineService promptEngineService;

	@Autowired
	private NutritionResultRepository nutritionResultRepository;
	
	@Autowired
	private GroundTruthIngredientRepository groundTruthRepo;
	
	@Autowired
    private TranscriptRepository transcriptRepo;

	@Autowired
	private ExperimentRepository experimentRepository;

	@Autowired
	private IngredientResultRepository ingredientResultRepository;

	@Autowired
	private ReelRepository reelRepository;

    @Autowired
    private edu.utem.ftmk.llm.repository.InfluencerRepository influencerRepository;

    @Autowired
    private AudioFileRepository audioFileRepository;

	@Autowired
	private edu.utem.ftmk.llm.repository.LlmModelRepository llmModelRepository;

	@Autowired
	private edu.utem.ftmk.llm.repository.PromptTechniqueRepository promptTechniqueRepository;

	@Autowired
	private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

	// --- Helper: extract a Float from a JsonNode safely ---
	private Float jsonFloat(JsonNode node, String field) {
		if (node != null && node.has(field) && !node.get(field).isNull()) {
			return (float) node.get(field).asDouble();
		}
		return null;
	}

	// --- Helper: extract all per-serving fields ---
	private void extractServingFields(JsonNode servingNode, NutritionResult result) {
		if (servingNode == null) return;
		result.setServingCalories(jsonFloat(servingNode, "calories"));
		result.setServingTotalFatG(jsonFloat(servingNode, "total_fat_g"));
		result.setServingSaturatedFatG(jsonFloat(servingNode, "saturated_fat_g"));
		result.setServingCholesterolMg(jsonFloat(servingNode, "cholesterol_mg"));
		result.setServingSodiumMg(jsonFloat(servingNode, "sodium_mg"));
		result.setServingCarbohydrateG(jsonFloat(servingNode, "total_carbohydrate_g"));
		result.setServingFiberG(jsonFloat(servingNode, "dietary_fiber_g"));
		result.setServingSugarsG(jsonFloat(servingNode, "total_sugars_g"));
		result.setServingProteinG(jsonFloat(servingNode, "protein_g"));
		result.setServingVitaminDMcg(jsonFloat(servingNode, "vitamin_d_mcg"));
		result.setServingCalciumMg(jsonFloat(servingNode, "calcium_mg"));
		result.setServingIronMg(jsonFloat(servingNode, "iron_mg"));
		result.setServingPotassiumMg(jsonFloat(servingNode, "potassium_mg"));
	}

	// --- Helper: extract all nutrition_total fields ---
	private void extractTotalFields(JsonNode totalNode, NutritionResult result) {
		if (totalNode == null) return;
		result.setTotalCalories(jsonFloat(totalNode, "calories"));
		result.setTotalFatG(jsonFloat(totalNode, "total_fat_g"));
		result.setTotalSaturatedFatG(jsonFloat(totalNode, "saturated_fat_g"));
		result.setTotalCholesterolMg(jsonFloat(totalNode, "cholesterol_mg"));
		result.setTotalSodiumMg(jsonFloat(totalNode, "sodium_mg"));
		result.setTotalCarbohydrateG(jsonFloat(totalNode, "total_carbohydrate_g"));
		result.setTotalFiberG(jsonFloat(totalNode, "dietary_fiber_g"));
		result.setTotalSugarsG(jsonFloat(totalNode, "total_sugars_g"));
		result.setTotalProteinG(jsonFloat(totalNode, "protein_g"));
		result.setTotalVitaminDMcg(jsonFloat(totalNode, "vitamin_d_mcg"));
		result.setTotalCalciumMg(jsonFloat(totalNode, "calcium_mg"));
		result.setTotalIronMg(jsonFloat(totalNode, "iron_mg"));
		result.setTotalPotassiumMg(jsonFloat(totalNode, "potassium_mg"));
	}

	// --- Helper: extract all ingredient fields from a JSON ingredient node ---
	private IngredientResult extractIngredient(JsonNode ingNode, Integer resultId) {
		IngredientResult ir = new IngredientResult();
		ir.setResultId(resultId);
		// Name fields — try both naming conventions from LLM output
		if (ingNode.has("ingredient_name_original") && !ingNode.get("ingredient_name_original").isNull())
			ir.setNameOriginal(ingNode.get("ingredient_name_original").asText());
		else if (ingNode.has("name_original") && !ingNode.get("name_original").isNull())
			ir.setNameOriginal(ingNode.get("name_original").asText());

		if (ingNode.has("ingredient_name_english") && !ingNode.get("ingredient_name_english").isNull())
			ir.setNameEn(ingNode.get("ingredient_name_english").asText());
		else if (ingNode.has("ingredient_name_en") && !ingNode.get("ingredient_name_en").isNull())
			ir.setNameEn(ingNode.get("ingredient_name_en").asText());
		else if (ingNode.has("name_en") && !ingNode.get("name_en").isNull())
			ir.setNameEn(ingNode.get("name_en").asText());

		// Quantity
		if (ingNode.has("quantity_value") && !ingNode.get("quantity_value").isNull())
			ir.setQuantityValue(jsonFloat(ingNode, "quantity_value"));

		// Unit fields
		if (ingNode.has("quantity_unit") && !ingNode.get("quantity_unit").isNull())
			ir.setUnitOriginal(ingNode.get("quantity_unit").asText());
		else if (ingNode.has("quantity_unit_original") && !ingNode.get("quantity_unit_original").isNull())
			ir.setUnitOriginal(ingNode.get("quantity_unit_original").asText());
		else if (ingNode.has("unit_original") && !ingNode.get("unit_original").isNull())
			ir.setUnitOriginal(ingNode.get("unit_original").asText());

		if (ingNode.has("unit_en") && !ingNode.get("unit_en").isNull())
			ir.setUnitEn(ingNode.get("unit_en").asText());
		else if (ingNode.has("quantity_unit_en") && !ingNode.get("quantity_unit_en").isNull())
			ir.setUnitEn(ingNode.get("quantity_unit_en").asText());

		// Metadata
		if (ingNode.has("language_tag") && !ingNode.get("language_tag").isNull())
			ir.setLanguageTag(ingNode.get("language_tag").asText());
		if (ingNode.has("hallucinated") && !ingNode.get("hallucinated").isNull())
			ir.setHallucinated(ingNode.get("hallucinated").asBoolean());

		// Weight & nutrition
		ir.setEstimatedWeightG(jsonFloat(ingNode, "estimated_weight_g"));
		ir.setCalories(jsonFloat(ingNode, "calories"));
		ir.setTotalFatG(jsonFloat(ingNode, "total_fat_g"));
		ir.setSaturatedFatG(jsonFloat(ingNode, "saturated_fat_g"));
		ir.setCholesterolMg(jsonFloat(ingNode, "cholesterol_mg"));
		ir.setSodiumMg(jsonFloat(ingNode, "sodium_mg"));
		ir.setTotalCarbohydrateG(jsonFloat(ingNode, "total_carbohydrate_g"));
		ir.setDietaryFiberG(jsonFloat(ingNode, "dietary_fiber_g"));
		ir.setTotalSugarsG(jsonFloat(ingNode, "total_sugars_g"));
		ir.setProteinG(jsonFloat(ingNode, "protein_g"));
		ir.setVitaminDMcg(jsonFloat(ingNode, "vitamin_d_mcg"));
		ir.setCalciumMg(jsonFloat(ingNode, "calcium_mg"));
		ir.setIronMg(jsonFloat(ingNode, "iron_mg"));
		ir.setPotassiumMg(jsonFloat(ingNode, "potassium_mg"));

		return ir;
	}

	private boolean isHallucinated(IngredientResult aiIngredient, List<GroundTruthIngredient> gtList) {
		if (gtList == null || gtList.isEmpty()) {
			return false; // Cannot evaluate if no ground truth exists
		}
		
		String aiNameOri = aiIngredient.getNameOriginal() != null ? aiIngredient.getNameOriginal().toLowerCase() : "";
		String aiNameEn = aiIngredient.getNameEn() != null ? aiIngredient.getNameEn().toLowerCase() : "";

		for (GroundTruthIngredient gt : gtList) {
			String gtNameOri = gt.getNameOriginal() != null ? gt.getNameOriginal().toLowerCase() : "";
			String gtNameEn = gt.getNameEn() != null ? gt.getNameEn().toLowerCase() : "";

			// Check original name match
			if (!aiNameOri.isEmpty() && !gtNameOri.isEmpty()) {
				if (aiNameOri.contains(gtNameOri) || gtNameOri.contains(aiNameOri)) return false;
			}
			// Check english name match
			if (!aiNameEn.isEmpty() && !gtNameEn.isEmpty()) {
				if (aiNameEn.contains(gtNameEn) || gtNameEn.contains(aiNameEn)) return false;
			}
		}
		// No match found in the entire ground truth list -> hallucinated
		return true;
	}
	
	@GetMapping("/reels")
	public List<Reel> getAllReels() {
		return reelRepository.findAll();
	}

	@GetMapping("/transcript/{reelId}")
	public String getTranscriptText(@PathVariable Integer reelId) {
		try {
			Transcript t = transcriptRepo.findByReelId(reelId).orElseThrow(() -> new RuntimeException("No transcript for reelId " + reelId));
			Path filePath = Paths.get("D:/DAD_Project/nutritional-llm-service/transcriptions/" + t.getFileName());
			if (Files.exists(filePath)) {
				return Files.readString(filePath);
			}
			return "File not found on disk.";
		} catch (Exception e) {
			return "Error reading transcript: " + e.getMessage();
		}
	}
	
	@GetMapping("/groundtruth/{gtReelId}")
    public List<GroundTruthIngredient> getGroundTruth(@PathVariable Integer gtReelId) {
        return groundTruthRepo.findByGtReelId(gtReelId);
    }
	
	/**
	 * Dynamic batch-ready endpoint that loads transcripts from disk using the database tracking ID
	 */
	@PostMapping("/analyze/{transcriptId}")
    public String analyzeTranscript(
            @PathVariable Integer transcriptId,
            @RequestParam(defaultValue = LLMService.LLAMA) String model,
            @RequestParam(defaultValue = "structured_output") String technique) {
        
        try {
            // Step 1: Fetch the specific transcript record from your database tracking
            Transcript transcriptRecord = transcriptRepo.findById(transcriptId)
                .orElseThrow(() -> new RuntimeException("Transcript not found in DB for ID: " + transcriptId));

            // Step 2: Read the actual .txt file from your local hard drive path
            Path filePath = Paths.get("C:/MasakGramPrompt/transcripts/" + transcriptRecord.getFileName());
            if (!Files.exists(filePath)) {
                return "Error: File does not exist on disk at " + filePath.toAbsolutePath();
            }
            String rawTranscriptText = Files.readString(filePath);

            // Step 3: Compile prompt through engine and process via your local Ollama server
            String formattedUserPrompt = promptEngineService.buildUserPrompt(technique, rawTranscriptText);
            String formattedSystemPrompt = promptEngineService.buildSystemPrompt(technique);
            String llmResponse = llmService.prompt(model, formattedSystemPrompt, formattedUserPrompt);
            
            // Step 4: Map the structured text output into your database entities
            NutritionResult result = new NutritionResult();
            result.setRawJsonOutput(llmResponse);
            result.setCreatedAt(LocalDateTime.now());
            result.setExperimentId(1); // Set to 1 matching your preloaded placeholder experiment row

            // Step 5: Extract and parse target nutrition facts fields safely
            String cleanJson = llmResponse;
            int startIdx = cleanJson.indexOf('{');
            int endIdx = cleanJson.lastIndexOf('}');
            if (startIdx != -1 && endIdx != -1 && endIdx >= startIdx) {
                cleanJson = cleanJson.substring(startIdx, endIdx + 1);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode rootNode = objectMapper.readTree(cleanJson);

                if (rootNode.has("recipe_name")) {
                    result.setRecipeName(rootNode.get("recipe_name").asText());
                }
                if (rootNode.has("servings_estimated")) {
                    result.setServingsEstimated(rootNode.get("servings_estimated").asInt());
                }

                extractServingFields(rootNode.has("amount_per_serving") ? rootNode.get("amount_per_serving") : null, result);
                extractTotalFields(rootNode.has("nutrition_total") ? rootNode.get("nutrition_total") : null, result);

                result.setJsonValid(true);
                nutritionResultRepository.save(result);

                if (rootNode.has("ingredients") && rootNode.get("ingredients").isArray()) {
                    List<GroundTruthIngredient> gtList = groundTruthRepo.findByGtReelId(transcriptRecord.getReelId());
                    for (JsonNode ingNode : rootNode.get("ingredients")) {
                        IngredientResult aiIngredient = extractIngredient(ingNode, result.getResultId());
                        aiIngredient.setHallucinated(isHallucinated(aiIngredient, gtList));
                        ingredientResultRepository.save(aiIngredient);
                    }
                }

            } catch (Exception jsonEx) {
                System.out.println("Warning: Output parsing failed or formatting was invalid JSON: " + jsonEx.getMessage());
                result.setJsonValid(false);
                nutritionResultRepository.save(result);
            }

            return llmResponse;
            
        } catch (Exception e) {
            return "Error during file tracking processing pipeline: " + e.getMessage();
        }
    }
	
	@GetMapping("/analyze/batch")
	public org.springframework.web.servlet.mvc.method.annotation.SseEmitter analyzeBatch(
			@RequestParam(defaultValue = LLMService.LLAMA) String model,
			@RequestParam(defaultValue = "structured_output") String technique) {
		
		org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter = new org.springframework.web.servlet.mvc.method.annotation.SseEmitter(0L); // 0L means infinite timeout

		new Thread(() -> {
			try {
				LocalDateTime batchTime = LocalDateTime.now();
				List<Transcript> transcripts = transcriptRepo.findAll();
				int successCount = 0;

				List<edu.utem.ftmk.llm.entity.LlmModel> dbModels = llmModelRepository.findAll();
				Integer baseModelId = dbModels.isEmpty() ? 0 : dbModels.get(0).getModelId();
				for (edu.utem.ftmk.llm.entity.LlmModel m : dbModels) {
					if (m.getModelTag().equalsIgnoreCase(model)) {
						baseModelId = m.getModelId();
						break;
					}
				}

				List<edu.utem.ftmk.llm.entity.PromptTechnique> dbTechniques = promptTechniqueRepository.findAll();
				Integer baseTechniqueId = dbTechniques.isEmpty() ? 0 : dbTechniques.get(0).getTechniqueId();
				String normalizedTechnique = technique.replace("_", "-");
				for (edu.utem.ftmk.llm.entity.PromptTechnique pt : dbTechniques) {
					if (pt.getTechniqueName().equalsIgnoreCase(normalizedTechnique)) {
						baseTechniqueId = pt.getTechniqueId();
						break;
					}
				}

				int total = transcripts.size();
				int current = 0;
				Integer firstExperimentId = null;

				for (Transcript transcript : transcripts) {
					current++;
					Experiment experiment = null;
					try {
						experiment = new Experiment();
						experiment.setModelName(model);
						experiment.setPromptTechnique(technique);
						experiment.setExecutedAt(batchTime);
						experiment.setTranscriptId(transcript.getTranscriptId());
						experiment.setModelId(baseModelId);
						experiment.setTechniqueId(baseTechniqueId);
						experiment = experimentRepository.save(experiment);

						// Update status to running
						experiment.setStatus("running");
						experimentRepository.save(experiment);						
						if (firstExperimentId == null) {
							firstExperimentId = experiment.getExperimentId();
						}
						Path path = Paths.get("transcriptions", transcript.getFileName());
						if (!Files.exists(path)) {
							path = Paths.get(transcript.getFilePath()); // fallback
						}

						if (Files.exists(path)) {
							String content = Files.readString(path);
							String formattedUserPrompt = promptEngineService.buildUserPrompt(technique, content);
							String formattedSystemPrompt = promptEngineService.buildSystemPrompt(technique);
							
							long startTime = System.currentTimeMillis();
							String llmResponse = llmService.prompt(model, formattedSystemPrompt, formattedUserPrompt);
							long processingTimeMs = System.currentTimeMillis() - startTime;

							NutritionResult result = new NutritionResult();
							result.setRawJsonOutput(llmResponse);
							result.setCreatedAt(LocalDateTime.now());
							result.setProcessingTimeMs(processingTimeMs);
							result.setExperimentId(experiment.getExperimentId());
							result.setReelId(transcript.getReelId());

							String cleanJson = llmResponse;
							int startIdx = cleanJson.indexOf('{');
							int endIdx = cleanJson.lastIndexOf('}');
							if (startIdx != -1 && endIdx != -1 && endIdx >= startIdx) {
								cleanJson = cleanJson.substring(startIdx, endIdx + 1);
							}

							try {
								ObjectMapper objectMapper = new ObjectMapper();
								JsonNode rootNode = objectMapper.readTree(cleanJson);

								if (rootNode.has("recipe_name")) {
									result.setRecipeName(rootNode.get("recipe_name").asText());
								}
								if (rootNode.has("servings_estimated")) {
									result.setServingsEstimated(rootNode.get("servings_estimated").asInt());
								}

								extractServingFields(rootNode.has("amount_per_serving") ? rootNode.get("amount_per_serving") : null, result);
								extractTotalFields(rootNode.has("nutrition_total") ? rootNode.get("nutrition_total") : null, result);

								result.setJsonValid(true);
								nutritionResultRepository.save(result);

								if (rootNode.has("ingredients") && rootNode.get("ingredients").isArray()) {
									List<GroundTruthIngredient> gtList = groundTruthRepo.findByGtReelId(transcript.getReelId());
									for (JsonNode ingNode : rootNode.get("ingredients")) {
										IngredientResult aiIngredient = extractIngredient(ingNode, result.getResultId());
										aiIngredient.setHallucinated(isHallucinated(aiIngredient, gtList));
										ingredientResultRepository.save(aiIngredient);
									}
								}
								// Success
								successCount++;
								experiment.setStatus("completed");
								experimentRepository.save(experiment);
							} catch (Exception parseEx) {
								// If JSON parsing fails, still save the raw LLM output to the DB, but mark it as invalid!
								result.setJsonValid(false);
								nutritionResultRepository.save(result);
								System.out.println("JSON parse failed for transcript " + transcript.getTranscriptId() + ": " + parseEx.getMessage());
								
								experiment.setStatus("failed");
								experimentRepository.save(experiment);
							}
						}
					} catch (Exception ex) {
						if (experiment != null) {
							experiment.setStatus("failed");
							try { experimentRepository.save(experiment); } catch (Exception ignored) {}
						}
						System.out.println("Failed to process transcript ID " + transcript.getTranscriptId() + ": " + ex.getMessage());
					} finally {
						int failedCount = current - successCount;
						int pendingCount = total - current;
						int runningCount = (current < total) ? 1 : 0;
						String progressJson = "{\"status\": \"progress\", \"current\": " + current + ", \"total\": " + total + ", \"currentFileName\": \"" + transcript.getFileName() + "\", \"completedCount\": " + successCount + ", \"failedCount\": " + failedCount + ", \"pendingCount\": " + pendingCount + ", \"runningCount\": " + runningCount + "}";
						try {
							emitter.send(org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event().name("progress").data(progressJson));
						} catch (Exception ignored) {}
					}
				}

				String successJson = "{\"status\": \"success\", \"experimentId\": " + (firstExperimentId != null ? firstExperimentId : 0) + ", \"processedCount\": " + successCount + ", \"failedCount\": " + (total - successCount) + "}";
				emitter.send(org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event().name("complete").data(successJson));
				emitter.complete();

			} catch (Exception e) {
				try {
					emitter.send(org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event().name("error").data("{\"status\": \"error\", \"message\": \"" + e.getMessage().replace("\"", "\\\"") + "\"}"));
					emitter.completeWithError(e);
				} catch (Exception ex) {
					emitter.completeWithError(ex);
				}
			}
		}).start();

		return emitter;
	}

	@GetMapping("/stats/processing-time")
	public List<Map<String, Object>> getProcessingTimeStats() {
		List<Experiment> experiments = experimentRepository.findAll();
		List<NutritionResult> results = nutritionResultRepository.findAll();

		List<Map<String, Object>> stats = new ArrayList<>();

		// Group experiments by their batch (model, technique, timestamp)
		Map<String, List<Experiment>> batches = experiments.stream()
			.collect(java.util.stream.Collectors.groupingBy(
				e -> e.getModelName() + "|" + e.getPromptTechnique() + "|" + e.getExecutedAt()
			));

		for (List<Experiment> batchExps : batches.values()) {
			if (batchExps.isEmpty()) continue;
			Experiment baseExp = batchExps.get(0);
			List<Integer> batchIds = batchExps.stream().map(Experiment::getExperimentId).collect(java.util.stream.Collectors.toList());

			List<NutritionResult> expResults = results.stream()
				.filter(r -> r.getExperimentId() != null && batchIds.contains(r.getExperimentId()))
				.filter(r -> r.getProcessingTimeMs() != null)
				.collect(java.util.stream.Collectors.toList());

			if (!expResults.isEmpty()) {
				double avgMs = expResults.stream().mapToLong(NutritionResult::getProcessingTimeMs).average().orElse(0.0);
				double avgSeconds = avgMs / 1000.0;

				Map<String, Object> stat = new HashMap<>();
				stat.put("experimentId", baseExp.getExperimentId());
				stat.put("modelName", baseExp.getModelName());
				stat.put("promptTechnique", baseExp.getPromptTechnique());
				stat.put("avgProcessingTimeSeconds", avgSeconds);
				stat.put("transcriptCount", expResults.size());
				stats.add(stat);
			}
		}

		return stats;
	}

	/**
	 * Fallback legacy endpoint for processing raw text payloads directly from standard textareas
	 */
	@PostMapping("/analyze")
	public String analyzeTranscriptText(
			@RequestParam(defaultValue = LLMService.LLAMA) String model,
			@RequestParam(defaultValue = "structured_output") String technique, 
			@RequestBody String transcript) {

		try {
			String formattedUserPrompt = promptEngineService.buildUserPrompt(technique, transcript);
			String formattedSystemPrompt = promptEngineService.buildSystemPrompt(technique);
			String llmResponse = llmService.prompt(model, formattedSystemPrompt, formattedUserPrompt);

			NutritionResult result = new NutritionResult();
			result.setRawJsonOutput(llmResponse);
			result.setCreatedAt(LocalDateTime.now());
			result.setExperimentId(1); 

            String cleanJson = llmResponse;
            int startIdx = cleanJson.indexOf('{');
            int endIdx = cleanJson.lastIndexOf('}');
            if (startIdx != -1 && endIdx != -1 && endIdx >= startIdx) {
                cleanJson = cleanJson.substring(startIdx, endIdx + 1);
            }

			ObjectMapper objectMapper = new ObjectMapper();
			try {
				JsonNode rootNode = objectMapper.readTree(cleanJson);

				if (rootNode.has("recipe_name")) {
					result.setRecipeName(rootNode.get("recipe_name").asText());
				}
				if (rootNode.has("servings_estimated")) {
					result.setServingsEstimated(rootNode.get("servings_estimated").asInt());
				}

				extractServingFields(rootNode.get("amount_per_serving"), result);
				extractTotalFields(rootNode.get("nutrition_total"), result);

				result.setJsonValid(true);
				
                nutritionResultRepository.save(result);

                if (rootNode.has("ingredients") && rootNode.get("ingredients").isArray()) {
                    for (JsonNode ingNode : rootNode.get("ingredients")) {
                        ingredientResultRepository.save(extractIngredient(ingNode, result.getResultId()));
                    }
                }

			} catch (Exception jsonEx) {
				System.out.println("Warning: Raw input parsing mismatch: " + jsonEx.getMessage());
				result.setJsonValid(false);
                nutritionResultRepository.save(result);
			}

			return llmResponse;

		} catch (Exception e) {
			return "Error during raw payload processing: " + e.getMessage();
		}
	}

	@GetMapping("/experiment/{experimentId}/results")
	public List<NutritionResult> getExperimentResults(@PathVariable Integer experimentId) {
		Experiment baseExp = experimentRepository.findById(experimentId).orElse(null);
		if (baseExp == null) {
			return new ArrayList<>();
		}
		
		// Find all experiment IDs in the same batch run (same model, technique, and timestamp)
		List<Integer> batchExperimentIds = experimentRepository.findAll().stream()
			.filter(e -> e.getModelName().equals(baseExp.getModelName())
					&& e.getPromptTechnique().equals(baseExp.getPromptTechnique())
					&& e.getExecutedAt().equals(baseExp.getExecutedAt()))
			.map(Experiment::getExperimentId)
			.collect(java.util.stream.Collectors.toList());

		return nutritionResultRepository.findAll().stream()
			.filter(r -> r.getExperimentId() != null && batchExperimentIds.contains(r.getExperimentId()))
			.collect(java.util.stream.Collectors.toList());
	}

    @GetMapping("/dashboard/reels")
    public org.springframework.http.ResponseEntity<?> getDashboardReels() {
        try {
            List<Reel> reels = reelRepository.findAll();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Reel reel : reels) {
                Map<String, Object> map = new HashMap<>();
                map.put("reelId", reel.getReelId());
                map.put("instagramId", reel.getReelIdInstagram());
                
                String influencerHandle = "Unknown";
                if (reel.getInfluencerId() != null) {
                    edu.utem.ftmk.llm.entity.Influencer influencer = influencerRepository.findById(reel.getInfluencerId()).orElse(null);
                    if (influencer != null) {
                        influencerHandle = influencer.getInstagramAccount();
                    }
                }
                map.put("influencerHandle", influencerHandle);
                
                // Check Ground Truth
                long gtCount = groundTruthRepo.findByGtReelId(reel.getReelId()).size();
                map.put("groundTruthAvailable", gtCount > 0);
                
                // Get Transcript info
                edu.utem.ftmk.llm.entity.Transcript t = transcriptRepo.findByReelId(reel.getReelId()).orElse(null);
                if (t != null) {
                    map.put("transcriptStatus", t.getAudioTranscriptConsistent() != null && t.getAudioTranscriptConsistent() ? "Consistent" : "Pending/Unverified");
                    
                    // Read transcript header
                    String duration = "Unknown";
                    String language = "Unknown";
                    try {
                        Path path = Paths.get(t.getFilePath());
                        if (Files.exists(path)) {
                            List<String> lines = Files.readAllLines(path);
                            for (int i=0; i < Math.min(15, lines.size()); i++) {
                                String line = lines.get(i);
                                if (line.startsWith("Duration")) {
                                    duration = line.split(":")[1].trim();
                                } else if (line.startsWith("Language")) {
                                    language = line.split(":")[1].trim();
                                }
                            }
                        }
                    } catch(Exception e) {
                        // Ignore
                    }
                    map.put("duration", duration);
                    map.put("language", language);
                } else {
                    map.put("transcriptStatus", "Missing");
                    map.put("duration", "N/A");
                    map.put("language", "N/A");
                }
                
                result.add(map);
            }
            return org.springframework.http.ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return org.springframework.http.ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/dashboard/reel/{id}")
    public org.springframework.http.ResponseEntity<?> getDashboardReelDetails(@PathVariable Integer id) {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // Transcript Text
            edu.utem.ftmk.llm.entity.Transcript t = transcriptRepo.findByReelId(id).orElse(null);
            String transcriptText = "Transcript not found.";
            if (t != null) {
                try {
                    Path path = Paths.get(t.getFilePath());
                    if (Files.exists(path)) {
                        String content = Files.readString(path);
                        int dividerIndex = content.indexOf("=====================================");
                        if (dividerIndex != -1) {
                            transcriptText = content.substring(dividerIndex + 37).trim();
                        } else {
                            transcriptText = content;
                        }
                    }
                } catch(Exception e) {
                    transcriptText = "Error reading transcript file: " + e.getMessage();
                }
            }
            result.put("transcriptText", transcriptText);
            
            // Analysis Statuses
            List<NutritionResult> nResults = nutritionResultRepository.findByReelId(id);
            List<Map<String, Object>> analysisList = new ArrayList<>();
            for (NutritionResult nr : nResults) {
                Map<String, Object> amap = new HashMap<>();
                amap.put("resultId", nr.getResultId());
                amap.put("experimentId", nr.getExperimentId());
                amap.put("jsonValid", nr.getJsonValid());
                amap.put("recipeName", nr.getRecipeName());
                
                // Fetch experiment to get Model and Technique
                if (nr.getExperimentId() != null) {
                    Experiment exp = experimentRepository.findById(nr.getExperimentId()).orElse(null);
                    if (exp != null) {
                        amap.put("model", exp.getModelName());
                        amap.put("technique", exp.getPromptTechnique());
                    } else {
                        amap.put("model", "Unknown");
                        amap.put("technique", "Unknown");
                    }
                } else {
                    amap.put("model", "Unknown");
                    amap.put("technique", "Unknown");
                }
                analysisList.add(amap);
            }
            result.put("analysisList", analysisList);
            
            return org.springframework.http.ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return org.springframework.http.ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/nutrition/{id}")
    public org.springframework.http.ResponseEntity<?> getNutritionResult(@PathVariable Integer id) {
        return nutritionResultRepository.findById(id)
            .map(res -> org.springframework.http.ResponseEntity.ok(res))
            .orElse(org.springframework.http.ResponseEntity.notFound().build());
    }

    @GetMapping("/experiment-data")
    public org.springframework.data.domain.Page<NutritionResult> getExperimentData(
            @RequestParam(required = false, defaultValue = "All") String model,
            @RequestParam(required = false, defaultValue = "All") String technique,
            @RequestParam(required = false, defaultValue = "All") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "resultId") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        
        Boolean jsonValid = null;
        if ("Success".equalsIgnoreCase(status)) {
            jsonValid = true;
        } else if ("Failed".equalsIgnoreCase(status)) {
            jsonValid = false;
        }
        
        org.springframework.data.domain.Sort sort = sortDir.equalsIgnoreCase("asc") 
                ? org.springframework.data.domain.Sort.by(sortBy).ascending() 
                : org.springframework.data.domain.Sort.by(sortBy).descending();

        return nutritionResultRepository.findByModelAndTechnique(
            model, technique, jsonValid, org.springframework.data.domain.PageRequest.of(page, size, sort));
    }

    @GetMapping("/experiment-detail/{experimentId}")
    public org.springframework.http.ResponseEntity<?> getExperimentDetail(@PathVariable Integer experimentId) {
        try {
            // 1. Fetch Experiment
            Experiment experiment = experimentRepository.findById(experimentId).orElse(null);
            if (experiment == null) {
                return org.springframework.http.ResponseEntity.notFound().build();
            }

            Map<String, Object> result = new HashMap<>();
            result.put("experiment", experiment);

            // 2. Fetch LLM Model by model_id
            if (experiment.getModelId() != null && experiment.getModelId() > 0) {
                llmModelRepository.findById(experiment.getModelId()).ifPresent(m -> result.put("llmModel", m));
            }

            // 3. Fetch Prompt Technique by technique_id
            if (experiment.getTechniqueId() != null && experiment.getTechniqueId() > 0) {
                promptTechniqueRepository.findById(experiment.getTechniqueId()).ifPresent(pt -> result.put("promptTechnique", pt));
            }

            // 4. Fetch Transcript by transcript_id
            Transcript transcript = null;
            if (experiment.getTranscriptId() != null && experiment.getTranscriptId() > 0) {
                transcript = transcriptRepo.findById(experiment.getTranscriptId()).orElse(null);
                if (transcript != null) {
                    result.put("transcript", transcript);
                }
            }

            // 5. Fetch Reel from transcript.reel_id
            Reel reel = null;
            if (transcript != null && transcript.getReelId() != null) {
                reel = reelRepository.findById(transcript.getReelId()).orElse(null);
                if (reel != null) {
                    result.put("reel", reel);
                }
            }

            // 6. Fetch Audio File from transcript.audio_id
            if (transcript != null && transcript.getAudioId() != null) {
                audioFileRepository.findById(transcript.getAudioId()).ifPresent(af -> result.put("audioFile", af));
            }

            // 7. Fetch Influencer from reel.influencer_id
            if (reel != null && reel.getInfluencerId() != null) {
                influencerRepository.findById(reel.getInfluencerId()).ifPresent(inf -> result.put("influencer", inf));
            }

            // 8. Fetch Nutrition Results for this experiment
            List<NutritionResult> nutritionResults = nutritionResultRepository.findByExperimentId(experimentId);
            result.put("nutritionResults", nutritionResults);

            // 9. Fetch Ingredient Results for each nutrition result
            List<Map<String, Object>> nutritionWithIngredients = new ArrayList<>();
            for (NutritionResult nr : nutritionResults) {
                Map<String, Object> nrMap = new HashMap<>();
                nrMap.put("nutritionResult", nr);
                nrMap.put("ingredients", ingredientResultRepository.findByResultId(nr.getResultId()));
                nutritionWithIngredients.add(nrMap);
            }
            result.put("nutritionWithIngredients", nutritionWithIngredients);

            return org.springframework.http.ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return org.springframework.http.ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

	@GetMapping("/export/{layerId}")
	public org.springframework.http.ResponseEntity<String> exportLayer(@PathVariable String layerId) {
		try {
			org.springframework.core.io.Resource resource = new org.springframework.core.io.ClassPathResource("static/db/metrics_evaluation_queries.sql");
			String sqlFile = new String(resource.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);

			String targetFileName = "layer" + layerId.toLowerCase();

			java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("OUTPUT FILE\\s*:\\s*(" + targetFileName + "[^\\n]*\\.csv)[\\s\\S]*?(?:-- NOTE: Uncomment.*?\\n)?[\\s\\S]*?(SELECT[\\s\\S]+?;)", java.util.regex.Pattern.CASE_INSENSITIVE);
			java.util.regex.Matcher matcher = pattern.matcher(sqlFile);

			if (!matcher.find()) {
				return org.springframework.http.ResponseEntity.notFound().build();
			}

			String fileName = matcher.group(1).trim();
			String query = matcher.group(2).trim();

			if (query.startsWith("--") || fileName.contains("layer4")) {
				return org.springframework.http.ResponseEntity.badRequest().body("Query is commented out or layer4 is a placeholder.");
			}

			java.util.List<java.util.Map<String, Object>> rows = jdbcTemplate.queryForList(query);

			if (rows.isEmpty()) {
				return org.springframework.http.ResponseEntity.ok("No data returned for " + fileName);
			}

			StringBuilder csv = new StringBuilder();
			java.util.Set<String> columns = rows.get(0).keySet();
			csv.append(String.join(",", columns)).append("\n");

			for (java.util.Map<String, Object> row : rows) {
				java.util.List<String> values = new java.util.ArrayList<>();
				for (String col : columns) {
					Object val = row.get(col);
					if (val == null) {
						values.add("");
					} else {
						String valStr = val.toString().replace("\"", "\"\"");
						valStr = valStr.replace("\r", " ").replace("\n", " ");
						values.add("\"" + valStr + "\"");
					}
				}
				csv.append(String.join(",", values)).append("\n");
			}

			return org.springframework.http.ResponseEntity.ok()
					.header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
					.header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "text/csv")
					.body(csv.toString());

		} catch (Exception e) {
			e.printStackTrace();
			return org.springframework.http.ResponseEntity.internalServerError().body("Error: " + e.getMessage());
		}
	}

	/**
	 * Returns side-by-side Ground Truth vs AI comparison data for a given experiment.
	 * Includes per-ingredient nutritional breakdown and match summary.
	 */
	@GetMapping("/comparison/{experimentId}")
	public org.springframework.http.ResponseEntity<?> getComparison(@PathVariable Integer experimentId) {
		try {
			Experiment experiment = experimentRepository.findById(experimentId).orElse(null);
			if (experiment == null) return org.springframework.http.ResponseEntity.notFound().build();

			Map<String, Object> result = new HashMap<>();
			result.put("experiment", experiment);

			// Get transcript → reel_id
			Transcript transcript = null;
			if (experiment.getTranscriptId() != null && experiment.getTranscriptId() > 0) {
				transcript = transcriptRepo.findById(experiment.getTranscriptId()).orElse(null);
			}

			// AI-predicted results
			List<NutritionResult> nutritionResults = nutritionResultRepository.findByExperimentId(experimentId);
			List<Map<String, Object>> aiIngredients = new ArrayList<>();
			for (NutritionResult nr : nutritionResults) {
				List<IngredientResult> irs = ingredientResultRepository.findByResultId(nr.getResultId());
				for (IngredientResult ir : irs) {
					Map<String, Object> m = new HashMap<>();
					m.put("nameOriginal", ir.getNameOriginal());
					m.put("nameEn", ir.getNameEn());
					m.put("quantityValue", ir.getQuantityValue());
					m.put("unitOriginal", ir.getUnitOriginal());
					m.put("calories", ir.getCalories());
					m.put("proteinG", ir.getProteinG());
					m.put("totalFatG", ir.getTotalFatG());
					m.put("totalCarbohydrateG", ir.getTotalCarbohydrateG());
					m.put("hallucinated", ir.getHallucinated());
					aiIngredients.add(m);
				}
				result.put("recipeName", nr.getRecipeName());
				result.put("totalCalories", nr.getTotalCalories());
				result.put("totalProteinG", nr.getTotalProteinG());
				result.put("totalFatG", nr.getTotalFatG());
				result.put("totalCarbohydrateG", nr.getTotalCarbohydrateG());
				result.put("jsonValid", nr.getJsonValid());
			}
			result.put("aiIngredients", aiIngredients);

			// Ground truth ingredients — look up via transcript → ground_truth_reel → ground_truth_ingredient
			List<Map<String, Object>> gtIngredients = new ArrayList<>();
			if (transcript != null) {
				// Use JdbcTemplate to query ground_truth_reel by transcript_id
				try {
					List<Map<String, Object>> gtReelRows = jdbcTemplate.queryForList(
						"SELECT gt_reel_id FROM ground_truth_reel WHERE transcript_id = ?",
						transcript.getTranscriptId()
					);
					for (Map<String, Object> gtRow : gtReelRows) {
						Integer gtReelId = ((Number) gtRow.get("gt_reel_id")).intValue();
						List<GroundTruthIngredient> gtis = groundTruthRepo.findByGtReelId(gtReelId);
						for (GroundTruthIngredient gti : gtis) {
							Map<String, Object> gm = new HashMap<>();
							gm.put("nameOriginal", gti.getNameOriginal());
							gm.put("nameEn", gti.getNameEn());
							gm.put("quantityValue", gti.getQuantityValueCulinary());
							gm.put("unitCulinary", gti.getQuantityUnitCulinary());
							gm.put("calories", gti.getCalories());
							gm.put("proteinG", gti.getProteinG());
							gm.put("totalFatG", gti.getTotalFatG());
							gm.put("totalCarbohydrateG", gti.getTotalCarbohydrateG());
							gtIngredients.add(gm);
						}
					}
				} catch (Exception ex) {
					System.out.println("Warning: Could not fetch ground truth: " + ex.getMessage());
				}
			}
			result.put("gtIngredients", gtIngredients);

			// Summary stats
			int aiCount = aiIngredients.size();
			int gtCount = gtIngredients.size();
			long hallucinatedCount = aiIngredients.stream()
				.filter(ai -> Boolean.TRUE.equals(ai.get("hallucinated")))
				.count();
			result.put("aiIngredientCount", aiCount);
			result.put("gtIngredientCount", gtCount);
			result.put("hallucinatedCount", hallucinatedCount);

			return org.springframework.http.ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			return org.springframework.http.ResponseEntity.internalServerError().body("Error: " + e.getMessage());
		}
	}

	/**
	 * Returns experiment data grouped by batch (model + technique + timestamp).
	 * Each batch row contains aggregated status counts.
	 */
	@GetMapping("/experiment-batches")
	public org.springframework.http.ResponseEntity<?> getExperimentBatches(
			@RequestParam(required = false, defaultValue = "All") String model,
			@RequestParam(required = false, defaultValue = "All") String technique) {
		try {
			List<Experiment> allExperiments = experimentRepository.findAll();

			// Filter
			List<Experiment> filtered = allExperiments.stream()
				.filter(e -> "All".equals(model) || model.equals(e.getModelName()))
				.filter(e -> "All".equals(technique) || technique.equals(e.getPromptTechnique()))
				.collect(java.util.stream.Collectors.toList());

			// Group by batch key
			Map<String, List<Experiment>> batches = filtered.stream()
				.collect(java.util.stream.Collectors.groupingBy(
					e -> e.getModelName() + "|" + e.getPromptTechnique() + "|" + e.getExecutedAt()
				));

			List<Map<String, Object>> result = new ArrayList<>();
			for (List<Experiment> batch : batches.values()) {
				if (batch.isEmpty()) continue;
				Experiment first = batch.get(0);
				Map<String, Object> batchMap = new HashMap<>();
				batchMap.put("firstExperimentId", first.getExperimentId());
				batchMap.put("modelName", first.getModelName());
				batchMap.put("promptTechnique", first.getPromptTechnique());
				batchMap.put("executedAt", first.getExecutedAt());
				batchMap.put("totalCount", batch.size());

				long completed = batch.stream().filter(e -> "completed".equals(e.getStatus())).count();
				long failed = batch.stream().filter(e -> "failed".equals(e.getStatus())).count();
				long pending = batch.stream().filter(e -> "pending".equals(e.getStatus())).count();
				long running = batch.stream().filter(e -> "running".equals(e.getStatus())).count();

				batchMap.put("completedCount", completed);
				batchMap.put("failedCount", failed);
				batchMap.put("pendingCount", pending);
				batchMap.put("runningCount", running);

				// Aggregated status label
				String aggStatus;
				if (completed == batch.size()) aggStatus = "All Completed";
				else if (failed == batch.size()) aggStatus = "All Failed";
				else if (running > 0) aggStatus = "Running";
				else aggStatus = completed + "/" + batch.size() + " Completed";
				batchMap.put("aggregatedStatus", aggStatus);

				result.add(batchMap);
			}

			// Sort by executedAt descending
			result.sort((a, b) -> {
				Object aDate = a.get("executedAt");
				Object bDate = b.get("executedAt");
				if (aDate == null && bDate == null) return 0;
				if (aDate == null) return 1;
				if (bDate == null) return -1;
				return bDate.toString().compareTo(aDate.toString());
			});

			return org.springframework.http.ResponseEntity.ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			return org.springframework.http.ResponseEntity.internalServerError().body("Error: " + e.getMessage());
		}
	}
}
