package edu.utem.ftmk.llm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ingredient_result")
public class IngredientResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    private Integer ingredientId;

    @Column(name = "result_id", nullable = false)
    private Integer resultId;

    // --- DB column: name_original ---
    @Column(name = "name_original", length = 200)
    private String nameOriginal;

    // --- DB column: name_en ---
    @Column(name = "name_en", length = 200)
    private String nameEn;

    @Column(name = "quantity_value")
    private Float quantityValue;

    @Column(name = "unit_original", length = 100)
    private String unitOriginal;

    @Column(name = "unit_en", length = 100)
    private String unitEn;

    @Column(name = "estimated_weight_g")
    private Float estimatedWeightG;

    @Column(name = "calories")
    private Float calories;

    @Column(name = "total_fat_g")
    private Float totalFatG;

    @Column(name = "saturated_fat_g")
    private Float saturatedFatG;

    @Column(name = "cholesterol_mg")
    private Float cholesterolMg;

    @Column(name = "sodium_mg")
    private Float sodiumMg;

    @Column(name = "total_carbohydrate_g")
    private Float totalCarbohydrateG;

    @Column(name = "dietary_fiber_g")
    private Float dietaryFiberG;

    @Column(name = "total_sugars_g")
    private Float totalSugarsG;

    @Column(name = "protein_g")
    private Float proteinG;

    @Column(name = "vitamin_d_mcg")
    private Float vitaminDMcg;

    @Column(name = "calcium_mg")
    private Float calciumMg;

    @Column(name = "iron_mg")
    private Float ironMg;

    @Column(name = "potassium_mg")
    private Float potassiumMg;

    @Column(name = "language_tag", length = 10)
    private String languageTag;

    @Column(name = "hallucinated")
    private Boolean hallucinated;

    // --- GETTERS AND SETTERS ---
    public Integer getIngredientId() { return ingredientId; }
    public void setIngredientId(Integer ingredientId) { this.ingredientId = ingredientId; }

    public Integer getResultId() { return resultId; }
    public void setResultId(Integer resultId) { this.resultId = resultId; }

    public String getNameOriginal() { return nameOriginal; }
    public void setNameOriginal(String nameOriginal) { this.nameOriginal = nameOriginal; }

    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    // Backward-compatible aliases used by existing JSON serialization
    public String getIngredientNameOriginal() { return nameOriginal; }
    public void setIngredientNameOriginal(String v) { this.nameOriginal = v; }

    public String getIngredientNameEnglish() { return nameEn; }
    public void setIngredientNameEnglish(String v) { this.nameEn = v; }

    public Float getQuantityValue() { return quantityValue; }
    public void setQuantityValue(Float quantityValue) { this.quantityValue = quantityValue; }

    // Backward-compatible String setter used by JSON parsing
    public void setQuantityValue(String quantityValue) {
        if (quantityValue != null && !quantityValue.isBlank()) {
            try { this.quantityValue = Float.parseFloat(quantityValue); }
            catch (NumberFormatException e) { this.quantityValue = null; }
        }
    }

    public String getUnitOriginal() { return unitOriginal; }
    public void setUnitOriginal(String unitOriginal) { this.unitOriginal = unitOriginal; }

    public String getUnitEn() { return unitEn; }
    public void setUnitEn(String unitEn) { this.unitEn = unitEn; }

    // Backward-compatible aliases for quantityUnit
    public String getQuantityUnit() { return unitOriginal; }
    public void setQuantityUnit(String quantityUnit) { this.unitOriginal = quantityUnit; }

    public Float getEstimatedWeightG() { return estimatedWeightG; }
    public void setEstimatedWeightG(Float estimatedWeightG) { this.estimatedWeightG = estimatedWeightG; }

    public Float getCalories() { return calories; }
    public void setCalories(Float calories) { this.calories = calories; }

    public Float getTotalFatG() { return totalFatG; }
    public void setTotalFatG(Float totalFatG) { this.totalFatG = totalFatG; }

    public Float getSaturatedFatG() { return saturatedFatG; }
    public void setSaturatedFatG(Float saturatedFatG) { this.saturatedFatG = saturatedFatG; }

    public Float getCholesterolMg() { return cholesterolMg; }
    public void setCholesterolMg(Float cholesterolMg) { this.cholesterolMg = cholesterolMg; }

    public Float getSodiumMg() { return sodiumMg; }
    public void setSodiumMg(Float sodiumMg) { this.sodiumMg = sodiumMg; }

    public Float getTotalCarbohydrateG() { return totalCarbohydrateG; }
    public void setTotalCarbohydrateG(Float totalCarbohydrateG) { this.totalCarbohydrateG = totalCarbohydrateG; }

    public Float getDietaryFiberG() { return dietaryFiberG; }
    public void setDietaryFiberG(Float dietaryFiberG) { this.dietaryFiberG = dietaryFiberG; }

    public Float getTotalSugarsG() { return totalSugarsG; }
    public void setTotalSugarsG(Float totalSugarsG) { this.totalSugarsG = totalSugarsG; }

    public Float getProteinG() { return proteinG; }
    public void setProteinG(Float proteinG) { this.proteinG = proteinG; }

    public Float getVitaminDMcg() { return vitaminDMcg; }
    public void setVitaminDMcg(Float vitaminDMcg) { this.vitaminDMcg = vitaminDMcg; }

    public Float getCalciumMg() { return calciumMg; }
    public void setCalciumMg(Float calciumMg) { this.calciumMg = calciumMg; }

    public Float getIronMg() { return ironMg; }
    public void setIronMg(Float ironMg) { this.ironMg = ironMg; }

    public Float getPotassiumMg() { return potassiumMg; }
    public void setPotassiumMg(Float potassiumMg) { this.potassiumMg = potassiumMg; }

    public String getLanguageTag() { return languageTag; }
    public void setLanguageTag(String languageTag) { this.languageTag = languageTag; }

    public Boolean getHallucinated() { return hallucinated; }
    public void setHallucinated(Boolean hallucinated) { this.hallucinated = hallucinated; }
}
