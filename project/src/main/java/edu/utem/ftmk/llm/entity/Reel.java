package edu.utem.ftmk.llm.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reel")
public class Reel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reel_id")
	private Integer reelId;

	@Column(name = "influencer_id", nullable = false)
	private Integer influencerId;

	@Column(name = "reel_id_instagram", nullable = false, length = 50)
	private String reelIdInstagram;

	@Column(name = "reel_url", nullable = false, length = 500)
	private String reelUrl;

	@Column(name = "identified_by_matric", nullable = false, length = 20)
	private String identifiedByMatric;

	@Column(name = "identified_by_name", nullable = false, length = 100)
	private String identifiedByName;

	@Column(name = "identified_date", nullable = false)
	private LocalDate identifiedDate;

	@Column(name = "created_at", insertable = false, updatable = false)
	private LocalDateTime createdAt;

	// --- GETTERS AND SETTERS ---
	public Integer getReelId() {
		return reelId;
	}

	public void setReelId(Integer reelId) {
		this.reelId = reelId;
	}

	public Integer getInfluencerId() {
		return influencerId;
	}

	public void setInfluencerId(Integer influencerId) {
		this.influencerId = influencerId;
	}

	public String getReelIdInstagram() {
		return reelIdInstagram;
	}

	public void setReelIdInstagram(String reelIdInstagram) {
		this.reelIdInstagram = reelIdInstagram;
	}

	public String getReelUrl() {
		return reelUrl;
	}

	public void setReelUrl(String reelUrl) {
		this.reelUrl = reelUrl;
	}

	public String getIdentifiedByMatric() {
		return identifiedByMatric;
	}

	public void setIdentifiedByMatric(String identifiedByMatric) {
		this.identifiedByMatric = identifiedByMatric;
	}

	public String getIdentifiedByName() {
		return identifiedByName;
	}

	public void setIdentifiedByName(String identifiedByName) {
		this.identifiedByName = identifiedByName;
	}

	public LocalDate getIdentifiedDate() {
		return identifiedDate;
	}

	public void setIdentifiedDate(LocalDate identifiedDate) {
		this.identifiedDate = identifiedDate;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}