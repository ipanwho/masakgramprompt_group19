package edu.utem.ftmk.llm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nutrition_result")
public class NutritionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Integer resultId;

    @Column(name = "experiment_id", nullable = false)
    private Integer experimentId; 

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "experiment_id", insertable = false, updatable = false)
    private Experiment experiment; 

    @Column(name = "reel_id", nullable = false)
    private Integer reelId;

    @Column(name = "recipe_name", length = 200)
    private String recipeName;

    @Column(name = "servings_estimated")
    private Integer servingsEstimated;

    // --- Per-Serving fields ---
    @Column(name = "serving_calories")
    private Float servingCalories;

    @Column(name = "serving_total_fat_g")
    private Float servingTotalFatG;

    @Column(name = "serving_saturated_fat_g")
    private Float servingSaturatedFatG;

    @Column(name = "serving_cholesterol_mg")
    private Float servingCholesterolMg;

    @Column(name = "serving_sodium_mg")
    private Float servingSodiumMg;

    @Column(name = "serving_carbohydrate_g")
    private Float servingCarbohydrateG;

    @Column(name = "serving_fiber_g")
    private Float servingFiberG;

    @Column(name = "serving_sugars_g")
    private Float servingSugarsG;

    @Column(name = "serving_protein_g")
    private Float servingProteinG;

    @Column(name = "serving_vitamin_d_mcg")
    private Float servingVitaminDMcg;

    @Column(name = "serving_calcium_mg")
    private Float servingCalciumMg;

    @Column(name = "serving_iron_mg")
    private Float servingIronMg;

    @Column(name = "serving_potassium_mg")
    private Float servingPotassiumMg;

    // --- Recipe Total fields ---
    @Column(name = "total_calories")
    private Float totalCalories;

    @Column(name = "total_fat_g")
    private Float totalFatG;

    @Column(name = "total_saturated_fat_g")
    private Float totalSaturatedFatG;

    @Column(name = "total_cholesterol_mg")
    private Float totalCholesterolMg;

    @Column(name = "total_sodium_mg")
    private Float totalSodiumMg;

    @Column(name = "total_carbohydrate_g")
    private Float totalCarbohydrateG;

    @Column(name = "total_fiber_g")
    private Float totalFiberG;

    @Column(name = "total_sugars_g")
    private Float totalSugarsG;

    @Column(name = "total_protein_g")
    private Float totalProteinG;

    @Column(name = "total_vitamin_d_mcg")
    private Float totalVitaminDMcg;

    @Column(name = "total_calcium_mg")
    private Float totalCalciumMg;

    @Column(name = "total_iron_mg")
    private Float totalIronMg;

    @Column(name = "total_potassium_mg")
    private Float totalPotassiumMg;

    // --- Metadata ---
    @Column(name = "raw_json_output", columnDefinition = "TEXT")
    private String rawJsonOutput;

    @Column(name = "json_valid")
    private Boolean jsonValid;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;

    // --- GETTERS AND SETTERS ---
    public Integer getResultId() { return resultId; }
    public void setResultId(Integer resultId) { this.resultId = resultId; }

    public Long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(Long processingTimeMs) { this.processingTimeMs = processingTimeMs; }

    public Integer getExperimentId() { return experimentId; }
    public void setExperimentId(Integer experimentId) { this.experimentId = experimentId; }

    public Experiment getExperiment() { return experiment; }
    public void setExperiment(Experiment experiment) { this.experiment = experiment; }

    public Integer getReelId() { return reelId; }
    public void setReelId(Integer reelId) { this.reelId = reelId; }

    public String getRecipeName() { return recipeName; }
    public void setRecipeName(String recipeName) { this.recipeName = recipeName; }

    public Integer getServingsEstimated() { return servingsEstimated; }
    public void setServingsEstimated(Integer servingsEstimated) { this.servingsEstimated = servingsEstimated; }

    // Per-Serving
    public Float getServingCalories() { return servingCalories; }
    public void setServingCalories(Float servingCalories) { this.servingCalories = servingCalories; }

    public Float getServingTotalFatG() { return servingTotalFatG; }
    public void setServingTotalFatG(Float servingTotalFatG) { this.servingTotalFatG = servingTotalFatG; }

    public Float getServingSaturatedFatG() { return servingSaturatedFatG; }
    public void setServingSaturatedFatG(Float v) { this.servingSaturatedFatG = v; }

    public Float getServingCholesterolMg() { return servingCholesterolMg; }
    public void setServingCholesterolMg(Float v) { this.servingCholesterolMg = v; }

    public Float getServingSodiumMg() { return servingSodiumMg; }
    public void setServingSodiumMg(Float v) { this.servingSodiumMg = v; }

    public Float getServingCarbohydrateG() { return servingCarbohydrateG; }
    public void setServingCarbohydrateG(Float v) { this.servingCarbohydrateG = v; }

    public Float getServingFiberG() { return servingFiberG; }
    public void setServingFiberG(Float v) { this.servingFiberG = v; }

    public Float getServingSugarsG() { return servingSugarsG; }
    public void setServingSugarsG(Float v) { this.servingSugarsG = v; }

    public Float getServingProteinG() { return servingProteinG; }
    public void setServingProteinG(Float servingProteinG) { this.servingProteinG = servingProteinG; }

    public Float getServingVitaminDMcg() { return servingVitaminDMcg; }
    public void setServingVitaminDMcg(Float v) { this.servingVitaminDMcg = v; }

    public Float getServingCalciumMg() { return servingCalciumMg; }
    public void setServingCalciumMg(Float v) { this.servingCalciumMg = v; }

    public Float getServingIronMg() { return servingIronMg; }
    public void setServingIronMg(Float v) { this.servingIronMg = v; }

    public Float getServingPotassiumMg() { return servingPotassiumMg; }
    public void setServingPotassiumMg(Float v) { this.servingPotassiumMg = v; }

    // Recipe Totals
    public Float getTotalCalories() { return totalCalories; }
    public void setTotalCalories(Float totalCalories) { this.totalCalories = totalCalories; }

    public Float getTotalFatG() { return totalFatG; }
    public void setTotalFatG(Float v) { this.totalFatG = v; }

    public Float getTotalSaturatedFatG() { return totalSaturatedFatG; }
    public void setTotalSaturatedFatG(Float v) { this.totalSaturatedFatG = v; }

    public Float getTotalCholesterolMg() { return totalCholesterolMg; }
    public void setTotalCholesterolMg(Float v) { this.totalCholesterolMg = v; }

    public Float getTotalSodiumMg() { return totalSodiumMg; }
    public void setTotalSodiumMg(Float v) { this.totalSodiumMg = v; }

    public Float getTotalCarbohydrateG() { return totalCarbohydrateG; }
    public void setTotalCarbohydrateG(Float v) { this.totalCarbohydrateG = v; }

    public Float getTotalFiberG() { return totalFiberG; }
    public void setTotalFiberG(Float v) { this.totalFiberG = v; }

    public Float getTotalSugarsG() { return totalSugarsG; }
    public void setTotalSugarsG(Float v) { this.totalSugarsG = v; }

    public Float getTotalProteinG() { return totalProteinG; }
    public void setTotalProteinG(Float totalProteinG) { this.totalProteinG = totalProteinG; }

    public Float getTotalVitaminDMcg() { return totalVitaminDMcg; }
    public void setTotalVitaminDMcg(Float v) { this.totalVitaminDMcg = v; }

    public Float getTotalCalciumMg() { return totalCalciumMg; }
    public void setTotalCalciumMg(Float v) { this.totalCalciumMg = v; }

    public Float getTotalIronMg() { return totalIronMg; }
    public void setTotalIronMg(Float v) { this.totalIronMg = v; }

    public Float getTotalPotassiumMg() { return totalPotassiumMg; }
    public void setTotalPotassiumMg(Float v) { this.totalPotassiumMg = v; }

    // Metadata
    public String getRawJsonOutput() { return rawJsonOutput; }
    public void setRawJsonOutput(String rawJsonOutput) { this.rawJsonOutput = rawJsonOutput; }

    public Boolean getJsonValid() { return jsonValid; }
    public void setJsonValid(Boolean jsonValid) { this.jsonValid = jsonValid; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}