package edu.utem.ftmk.llm.repository;

import edu.utem.ftmk.llm.entity.NutritionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface NutritionResultRepository extends JpaRepository<NutritionResult, Integer> {
    List<NutritionResult> findByReelId(Integer reelId);

    @Query("SELECT n FROM NutritionResult n WHERE (:status IS NULL OR n.jsonValid = :status) AND n.experimentId IN (SELECT e.experimentId FROM Experiment e WHERE " +
           "(:modelName IS NULL OR :modelName = 'All' OR e.modelName = :modelName) AND " +
           "(:promptTechnique IS NULL OR :promptTechnique = 'All' OR e.promptTechnique = :promptTechnique))")
    Page<NutritionResult> findByModelAndTechnique(@Param("modelName") String modelName, @Param("promptTechnique") String promptTechnique, @Param("status") Boolean status, Pageable pageable);

    List<NutritionResult> findByExperimentId(Integer experimentId);
}