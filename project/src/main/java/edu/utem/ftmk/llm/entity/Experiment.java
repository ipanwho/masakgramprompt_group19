package edu.utem.ftmk.llm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "experiment")
public class Experiment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "experiment_id")
    private Integer experimentId;

    @Column(name = "model_name", nullable = false)
    private String modelName;

    @Column(name = "prompt_technique", nullable = false)
    private String promptTechnique;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    @Column(name = "rag_enabled")
    private Boolean ragEnabled = false;

    @Column(name = "status", length = 20)
    private String status = "pending";

    // Legacy fields to bypass DB 'NOT NULL' constraints from previous schema versions
    @Column(name = "transcript_id")
    private Integer transcriptId = 0;
    
    @Column(name = "model_id")
    private Integer modelId = 0;
    
    @Column(name = "technique_id")
    private Integer techniqueId = 0;

    // --- GETTERS AND SETTERS ---
    public Integer getTranscriptId() { return transcriptId; }
    public void setTranscriptId(Integer transcriptId) { this.transcriptId = transcriptId; }

    public Integer getModelId() { return modelId; }
    public void setModelId(Integer modelId) { this.modelId = modelId; }

    public Integer getTechniqueId() { return techniqueId; }
    public void setTechniqueId(Integer techniqueId) { this.techniqueId = techniqueId; }

    public Integer getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(Integer experimentId) {
        this.experimentId = experimentId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getPromptTechnique() {
        return promptTechnique;
    }

    public void setPromptTechnique(String promptTechnique) {
        this.promptTechnique = promptTechnique;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }

    public Boolean getRagEnabled() {
        return ragEnabled;
    }

    public void setRagEnabled(Boolean ragEnabled) {
        this.ragEnabled = ragEnabled;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
