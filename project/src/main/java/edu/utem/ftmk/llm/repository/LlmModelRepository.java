package edu.utem.ftmk.llm.repository;

import edu.utem.ftmk.llm.entity.LlmModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LlmModelRepository extends JpaRepository<LlmModel, Integer> {
    // Spring automatically gives us save(), findAll(), findById(), etc.
    // You can add custom finders later, e.g.: LlmModel findByModelTag(String tag);
}