-- ============================================================
-- FILE    : metrics_evaluation_queries.sql
-- PROJECT : Nutritional LLM Analysis
-- PURPOSE : SQL queries for metric computation per evaluation
--           layer. Each query produces one CSV export that
--           feeds directly into evaluate.py.
-- AUTHOR  : EMALIANA KASMURI
-- ============================================================


-- ============================================================
-- LAYER 1A : EXACT MATCH (EM)
-- OUTPUT FILE : layer1a_exact_match.csv
-- PURPOSE : Extracts predicted vs ground truth ingredient name
--           and unit fields for exact match computation.
--           One row per ingredient pair per experiment.
-- ============================================================
SELECT
    e.experiment_id,
    e.transcript_id,
    t.reel_id,
    m.model_name,
    pt.technique_name,
    e.rag_enabled,

    -- Ground truth fields (reference)
    gti.name_original        AS gt_name_original,
    gti.name_en              AS gt_name_en,
    gti.quantity_expression   AS gt_quantity_expression,
    gti.quantity_unit_culinary AS gt_unit_culinary,

    -- LLM predicted fields
    ir.name_original         AS pred_name_original,
    ir.name_en               AS pred_name_en,
    ir.unit_original         AS pred_unit_original,
    ir.unit_en               AS pred_unit_en,
    ir.hallucinated          AS pred_hallucinated

FROM experiment e
JOIN transcript t               ON e.transcript_id     = t.transcript_id
JOIN llm_model m                ON e.model_id          = m.model_id
JOIN prompt_technique pt        ON e.technique_id      = pt.technique_id
JOIN nutrition_result nr        ON e.experiment_id     = nr.experiment_id
JOIN ingredient_result ir       ON nr.result_id        = ir.result_id
JOIN ground_truth_reel gtr      ON t.transcript_id     = gtr.transcript_id
JOIN ground_truth_ingredient gti ON gtr.gt_reel_id = gti.gt_reel_id

WHERE e.status = 'completed'
ORDER BY e.experiment_id, gti.gt_ingredient_id;


-- ============================================================
-- LAYER 1B : FUZZY MATCH & BLEU / ROUGE
-- OUTPUT FILE : layer1b_text_similarity.csv
-- PURPOSE : Extracts all free-text ingredient fields for
--           fuzzy match, BLEU-1, BLEU-2, ROUGE-1, ROUGE-L
--           computation in evaluate.py.
-- ============================================================
SELECT
    e.experiment_id,
    t.reel_id,
    m.model_name,
    pt.technique_name,
    e.rag_enabled,

    -- Ground truth text fields
    gti.name_original        AS gt_name_original,
    gti.name_en              AS gt_name_en,

    -- Predicted text fields
    ir.name_original         AS pred_name_original,
    ir.name_en               AS pred_name_en,
    ir.hallucinated          AS pred_hallucinated

FROM experiment e
JOIN transcript t               ON e.transcript_id     = t.transcript_id
JOIN llm_model m                ON e.model_id          = m.model_id
JOIN prompt_technique pt        ON e.technique_id      = pt.technique_id
JOIN nutrition_result nr        ON e.experiment_id     = nr.experiment_id
JOIN ingredient_result ir       ON nr.result_id        = ir.result_id
JOIN ground_truth_reel gtr      ON t.transcript_id     = gtr.transcript_id
JOIN ground_truth_ingredient gti ON gtr.gt_reel_id = gti.gt_reel_id

WHERE e.status = 'completed'
ORDER BY e.experiment_id, gti.gt_ingredient_id;


-- ============================================================
-- LAYER 2A : MAE & MAPE — QUANTITY & WEIGHT
-- OUTPUT FILE : layer2a_numeric_quantity.csv
-- PURPOSE : Extracts quantity_value and estimated_weight_g
--           pairs for MAE and MAPE computation.
-- ============================================================
SELECT
    e.experiment_id,
    t.reel_id,
    m.model_name,
    pt.technique_name,
    e.rag_enabled,

    -- Ground truth numeric fields
    gti.quantity_value_culinary AS gt_quantity_value,
    gti.estimated_weight_g   AS gt_weight_g,

    -- Predicted numeric fields
    ir.quantity_value        AS pred_quantity_value,
    ir.estimated_weight_g    AS pred_weight_g

FROM experiment e
JOIN transcript t               ON e.transcript_id     = t.transcript_id
JOIN llm_model m                ON e.model_id          = m.model_id
JOIN prompt_technique pt        ON e.technique_id      = pt.technique_id
JOIN nutrition_result nr        ON e.experiment_id     = nr.experiment_id
JOIN ingredient_result ir       ON nr.result_id        = ir.result_id
JOIN ground_truth_reel gtr      ON t.transcript_id     = gtr.transcript_id
JOIN ground_truth_ingredient gti ON gtr.gt_reel_id = gti.gt_reel_id

WHERE e.status = 'completed'
  AND gti.annotation_layer = 'layer2'
ORDER BY e.experiment_id, gti.gt_ingredient_id;


-- ============================================================
-- LAYER 2B : MAE, MAPE & PEARSON — NUTRITION VALUES
-- OUTPUT FILE : layer2b_numeric_nutrition.csv
-- PURPOSE : Extracts per-ingredient nutrition values for
--           MAE, MAPE, and Pearson correlation per nutrient
--           per model and prompt technique.
-- ============================================================
SELECT
    e.experiment_id,
    t.reel_id,
    m.model_name,
    pt.technique_name,
    e.rag_enabled,

    -- Ground truth nutrition values
    gti.calories             AS gt_energy_kcal,
    gti.protein_g            AS gt_protein_g,
    gti.total_fat_g          AS gt_fat_g,
    gti.total_carbohydrate_g AS gt_carbohydrate_g,

    -- Predicted nutrition values
    ir.calories              AS pred_energy_kcal,
    ir.protein_g             AS pred_protein_g,
    ir.total_fat_g           AS pred_fat_g,
    ir.total_carbohydrate_g  AS pred_carbohydrate_g

FROM experiment e
JOIN transcript t               ON e.transcript_id     = t.transcript_id
JOIN llm_model m                ON e.model_id          = m.model_id
JOIN prompt_technique pt        ON e.technique_id      = pt.technique_id
JOIN nutrition_result nr        ON e.experiment_id     = nr.experiment_id
JOIN ingredient_result ir       ON nr.result_id        = ir.result_id
JOIN ground_truth_reel gtr      ON t.transcript_id     = gtr.transcript_id
JOIN ground_truth_ingredient gti ON gtr.gt_reel_id = gti.gt_reel_id

WHERE e.status = 'completed'
  AND gti.annotation_layer = 'layer2'
ORDER BY e.experiment_id, gti.gt_ingredient_id;


-- ============================================================
-- LAYER 2C : RECIPE-LEVEL NUTRITION TOTALS
-- OUTPUT FILE : layer2c_nutrition_totals.csv
-- PURPOSE : Compares aggregated recipe-level totals between
--           ground truth and LLM output. Used for overall
--           recipe accuracy reporting in the paper.
-- ============================================================
SELECT
    e.experiment_id,
    t.reel_id,
    m.model_name,
    pt.technique_name,
    e.rag_enabled,

    -- Ground truth recipe totals (summed from ingredient level)
    SUM(gti.calories)            AS gt_total_energy_kcal,
    SUM(gti.protein_g)           AS gt_total_protein_g,
    SUM(gti.total_fat_g)         AS gt_total_fat_g,
    SUM(gti.total_carbohydrate_g) AS gt_total_carbohydrate_g,

    -- Predicted recipe totals (stored in nutrition_result)
    nr.total_calories            AS pred_total_energy_kcal,
    nr.total_protein_g           AS pred_total_protein_g,
    nr.total_fat_g               AS pred_total_fat_g,
    nr.total_carbohydrate_g      AS pred_total_carbohydrate_g

FROM experiment e
JOIN transcript t               ON e.transcript_id     = t.transcript_id
JOIN llm_model m                ON e.model_id          = m.model_id
JOIN prompt_technique pt        ON e.technique_id      = pt.technique_id
JOIN nutrition_result nr        ON e.experiment_id     = nr.experiment_id
JOIN ground_truth_reel gtr      ON t.transcript_id     = gtr.transcript_id
JOIN ground_truth_ingredient gti ON gtr.gt_reel_id = gti.gt_reel_id

WHERE e.status = 'completed'
  AND gti.annotation_layer = 'layer2'
GROUP BY
    e.experiment_id, t.reel_id, m.model_name,
    pt.technique_name, e.rag_enabled,
    nr.total_calories, nr.total_protein_g,
    nr.total_fat_g, nr.total_carbohydrate_g
ORDER BY e.experiment_id;


-- ============================================================
-- LAYER 3A : JSON VALIDITY RATE
-- OUTPUT FILE : layer3a_json_validity.csv
-- PURPOSE : Counts valid vs invalid JSON outputs per model
--           and prompt technique. Computes validity rate
--           as a percentage.
-- ============================================================
SELECT
    m.model_name,
    pt.technique_name,
    e.rag_enabled,
    COUNT(*)                                      AS total_runs,
    SUM(CASE WHEN nr.json_valid = TRUE THEN 1
             ELSE 0 END)                          AS valid_count,
    SUM(CASE WHEN nr.json_valid = FALSE THEN 1
             ELSE 0 END)                          AS invalid_count,
    ROUND(
        SUM(CASE WHEN nr.json_valid = TRUE THEN 1
                 ELSE 0 END) * 100.0 / COUNT(*), 2
    )                                             AS validity_rate_pct

FROM experiment e
JOIN llm_model m           ON e.model_id      = m.model_id
JOIN prompt_technique pt   ON e.technique_id  = pt.technique_id
JOIN nutrition_result nr   ON e.experiment_id = nr.experiment_id

WHERE e.status = 'completed'
GROUP BY m.model_name, pt.technique_name, e.rag_enabled
ORDER BY m.model_name, pt.technique_name;


-- ============================================================
-- LAYER 3B : HALLUCINATION RATE
-- OUTPUT FILE : layer3b_hallucination.csv
-- PURPOSE : Extracts is_hallucinated flag per ingredient
--           result. evaluate.py aggregates into hallucination
--           rate per model and prompt technique.
-- ============================================================
SELECT
    e.experiment_id,
    t.reel_id,
    m.model_name,
    pt.technique_name,
    e.rag_enabled,
    ir.name_original         AS pred_name_original,
    ir.name_en               AS pred_name_en,
    ir.hallucinated

FROM experiment e
JOIN transcript t           ON e.transcript_id  = t.transcript_id
JOIN llm_model m            ON e.model_id       = m.model_id
JOIN prompt_technique pt    ON e.technique_id   = pt.technique_id
JOIN nutrition_result nr    ON e.experiment_id  = nr.experiment_id
JOIN ingredient_result ir   ON nr.result_id     = ir.result_id

WHERE e.status = 'completed'
ORDER BY e.experiment_id, ir.ingredient_id;


-- ============================================================
-- LAYER 3C : INGREDIENT PRECISION, RECALL & F1
-- OUTPUT FILE : layer3c_ingredient_detection.csv
-- PURPOSE : Provides ingredient counts per experiment for
--           precision, recall, and F1 computation.
--           TP = matched ingredients (not hallucinated)
--           FP = hallucinated ingredients
--           FN = ground truth ingredients not found by LLM
-- NOTE    : Uses subqueries to avoid cartesian product between
--           predicted and ground truth ingredient rows.
-- ============================================================
SELECT
    e.experiment_id,
    t.reel_id,
    m.model_name,
    pt.technique_name,
    e.rag_enabled,

    -- Ground truth ingredient count (from subquery)
    COALESCE(gt_stats.gt_ingredient_count, 0) AS gt_ingredient_count,

    -- Predicted ingredient count (from subquery)
    COALESCE(pred_stats.pred_ingredient_count, 0) AS pred_ingredient_count,

    -- True positives (predicted and not hallucinated)
    COALESCE(pred_stats.true_positives, 0)   AS true_positives,

    -- False positives (hallucinated)
    COALESCE(pred_stats.false_positives, 0)  AS false_positives

FROM experiment e
JOIN transcript t               ON e.transcript_id     = t.transcript_id
JOIN llm_model m                ON e.model_id          = m.model_id
JOIN prompt_technique pt        ON e.technique_id      = pt.technique_id
JOIN nutrition_result nr        ON e.experiment_id     = nr.experiment_id

-- Subquery: predicted ingredient stats per nutrition_result
LEFT JOIN (
    SELECT
        ir2.result_id,
        COUNT(DISTINCT ir2.ingredient_id)                                    AS pred_ingredient_count,
        SUM(CASE WHEN ir2.hallucinated = FALSE THEN 1 ELSE 0 END)           AS true_positives,
        SUM(CASE WHEN ir2.hallucinated = TRUE  THEN 1 ELSE 0 END)           AS false_positives
    FROM ingredient_result ir2
    GROUP BY ir2.result_id
) pred_stats ON nr.result_id = pred_stats.result_id

-- Subquery: ground truth ingredient count per transcript
LEFT JOIN ground_truth_reel gtr ON t.transcript_id = gtr.transcript_id
LEFT JOIN (
    SELECT
        gti2.gt_reel_id,
        COUNT(DISTINCT gti2.gt_ingredient_id) AS gt_ingredient_count
    FROM ground_truth_ingredient gti2
    GROUP BY gti2.gt_reel_id
) gt_stats ON gtr.gt_reel_id = gt_stats.gt_reel_id

WHERE e.status = 'completed'
GROUP BY
    e.experiment_id, t.reel_id, m.model_name,
    pt.technique_name, e.rag_enabled,
    gt_stats.gt_ingredient_count,
    pred_stats.pred_ingredient_count,
    pred_stats.true_positives,
    pred_stats.false_positives
ORDER BY e.experiment_id;


-- ============================================================
-- LAYER 4 : HUMAN EVALUATION — LIKERT & KRIPPENDORFF
-- OUTPUT FILE : layer4_human_evaluation.csv
-- PURPOSE : Placeholder query — to be populated after
--           human_evaluation table is created in Phase 2.
--           Structure shown here for planning purposes.
--           Annotators rate each nutrition_result on three
--           dimensions: fluency, completeness, plausibility.
-- ============================================================
-- NOTE: Uncomment and run after human_evaluation table exists.
--
-- SELECT
--     he.evaluation_id,
--     he.result_id,
--     e.experiment_id,
--     t.reel_id,
--     m.model_name,
--     pt.technique_name,
--     he.annotator_id,
--     he.fluency_score,
--     he.completeness_score,
--     he.plausibility_score,
--     he.evaluated_at
-- FROM human_evaluation he
-- JOIN nutrition_result nr   ON he.result_id     = nr.result_id
-- JOIN experiment e          ON nr.experiment_id = e.experiment_id
-- JOIN transcript t          ON e.transcript_id  = t.transcript_id
-- JOIN llm_model m           ON e.model_id       = m.model_id
-- JOIN prompt_technique pt   ON e.technique_id   = pt.technique_id
-- ORDER BY he.result_id, he.annotator_id;


-- ============================================================
-- LAYER 5 : STATISTICAL SIGNIFICANCE — FRIEDMAN & WILCOXON
-- OUTPUT FILE : layer5_condition_scores.csv
-- PURPOSE : Aggregates mean F1 score per condition (model ×
--           technique) across all transcripts. This is the
--           input matrix for the Friedman test in evaluate.py.
--           One row per transcript, one column per condition.
-- NOTE    : Uses subqueries to avoid cartesian product between
--           predicted and ground truth ingredient rows.
-- ============================================================
SELECT
    t.reel_id,
    m.model_name,
    pt.technique_name,
    e.rag_enabled,

    -- Predicted ingredient stats (from subquery)
    COALESCE(pred_stats.pred_count, 0)       AS pred_count,
    COALESCE(pred_stats.true_positives, 0)   AS true_positives,
    COALESCE(pred_stats.false_positives, 0)  AS false_positives,

    -- Ground truth count (from subquery)
    COALESCE(gt_stats.gt_count, 0)           AS gt_count,

    nr.json_valid,
    nr.total_calories                        AS pred_total_kcal,
    COALESCE(gt_stats.gt_total_kcal, 0)      AS gt_total_kcal

FROM experiment e
JOIN transcript t               ON e.transcript_id     = t.transcript_id
JOIN llm_model m                ON e.model_id          = m.model_id
JOIN prompt_technique pt        ON e.technique_id      = pt.technique_id
JOIN nutrition_result nr        ON e.experiment_id     = nr.experiment_id

-- Subquery: predicted ingredient stats per nutrition_result
LEFT JOIN (
    SELECT
        ir2.result_id,
        COUNT(*)                                                    AS pred_count,
        SUM(CASE WHEN ir2.hallucinated = FALSE THEN 1 ELSE 0 END)  AS true_positives,
        SUM(CASE WHEN ir2.hallucinated = TRUE  THEN 1 ELSE 0 END)  AS false_positives
    FROM ingredient_result ir2
    GROUP BY ir2.result_id
) pred_stats ON nr.result_id = pred_stats.result_id

-- Subquery: ground truth stats per transcript
LEFT JOIN ground_truth_reel gtr ON t.transcript_id = gtr.transcript_id
LEFT JOIN (
    SELECT
        gti2.gt_reel_id,
        COUNT(DISTINCT gti2.gt_ingredient_id) AS gt_count,
        SUM(gti2.calories)                    AS gt_total_kcal
    FROM ground_truth_ingredient gti2
    WHERE gti2.annotation_layer = 'layer2'
    GROUP BY gti2.gt_reel_id
) gt_stats ON gtr.gt_reel_id = gt_stats.gt_reel_id

WHERE e.status = 'completed'
ORDER BY t.reel_id, m.model_name, pt.technique_name;
