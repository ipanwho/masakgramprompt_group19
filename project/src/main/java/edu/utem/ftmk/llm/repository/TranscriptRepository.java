package edu.utem.ftmk.llm.repository;

import edu.utem.ftmk.llm.entity.Transcript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TranscriptRepository extends JpaRepository<Transcript, Integer> {
    Optional<Transcript> findByReelId(Integer reelId);
}