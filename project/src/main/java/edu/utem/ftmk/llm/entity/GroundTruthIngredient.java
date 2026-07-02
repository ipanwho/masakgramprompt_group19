package edu.utem.ftmk.llm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ground_truth_ingredient")
public class GroundTruthIngredient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "gt_ingredient_id")
	private Integer gtIngredientId;

	@Column(name = "gt_reel_id", nullable = false)
	private Integer gtReelId;

	@Column(name = "name_original", nullable = false, length = 200)
	private String nameOriginal;

	@Column(name = "language_mentioned", nullable = false, length = 5)
	private String languageMentioned;

	@Column(name = "name_en", length = 200)
	private String nameEn;

	@Column(name = "quantity_expression", length = 200)
	private String quantityExpression;

	@Column(name = "quantity_category", length = 50)
	private String quantityCategory;

	@Column(name = "quantity_unit_culinary", length = 100)
	private String quantityUnitCulinary;

	@Column(name = "quantity_value_culinary")
	private Float quantityValueCulinary;

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

	@Column(name = "annotation_layer", nullable = false, length = 10)
	private String annotationLayer;

	@Column(name = "annotator_matric", nullable = false, length = 20)
	private String annotatorMatric;

	@Column(name = "annotator_name", nullable = false, length = 100)
	private String annotatorName;

	@Column(name = "annotated_at")
	private LocalDateTime annotatedAt;

	// --- GETTERS AND SETTERS ---
	public Integer getGtIngredientId() { return gtIngredientId; }
	public void setGtIngredientId(Integer gtIngredientId) { this.gtIngredientId = gtIngredientId; }

	public Integer getGtReelId() { return gtReelId; }
	public void setGtReelId(Integer gtReelId) { this.gtReelId = gtReelId; }

	public String getNameOriginal() { return nameOriginal; }
	public void setNameOriginal(String nameOriginal) { this.nameOriginal = nameOriginal; }

	public String getLanguageMentioned() { return languageMentioned; }
	public void setLanguageMentioned(String languageMentioned) { this.languageMentioned = languageMentioned; }

	public String getNameEn() { return nameEn; }
	public void setNameEn(String nameEn) { this.nameEn = nameEn; }

	public String getQuantityExpression() { return quantityExpression; }
	public void setQuantityExpression(String quantityExpression) { this.quantityExpression = quantityExpression; }

	public String getQuantityCategory() { return quantityCategory; }
	public void setQuantityCategory(String quantityCategory) { this.quantityCategory = quantityCategory; }

	public String getQuantityUnitCulinary() { return quantityUnitCulinary; }
	public void setQuantityUnitCulinary(String quantityUnitCulinary) { this.quantityUnitCulinary = quantityUnitCulinary; }

	public Float getQuantityValueCulinary() { return quantityValueCulinary; }
	public void setQuantityValueCulinary(Float quantityValueCulinary) { this.quantityValueCulinary = quantityValueCulinary; }

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

	public String getAnnotationLayer() { return annotationLayer; }
	public void setAnnotationLayer(String annotationLayer) { this.annotationLayer = annotationLayer; }

	public String getAnnotatorMatric() { return annotatorMatric; }
	public void setAnnotatorMatric(String annotatorMatric) { this.annotatorMatric = annotatorMatric; }

	public String getAnnotatorName() { return annotatorName; }
	public void setAnnotatorName(String annotatorName) { this.annotatorName = annotatorName; }

	public LocalDateTime getAnnotatedAt() { return annotatedAt; }
	public void setAnnotatedAt(LocalDateTime annotatedAt) { this.annotatedAt = annotatedAt; }
}