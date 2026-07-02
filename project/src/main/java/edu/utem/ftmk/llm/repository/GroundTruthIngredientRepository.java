package edu.utem.ftmk.llm.repository;

import edu.utem.ftmk.llm.entity.GroundTruthIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GroundTruthIngredientRepository extends JpaRepository<GroundTruthIngredient, Integer> {
    // Custom method to fetch all ingredients belonging to a specific reel
    List<GroundTruthIngredient> findByGtReelId(Integer gtReelId);
}