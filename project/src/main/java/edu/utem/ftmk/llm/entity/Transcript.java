package edu.utem.ftmk.llm.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "transcript")
public class Transcript {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "transcript_id")
	private Integer transcriptId;

	@Column(name = "reel_id", nullable = false)
	private Integer reelId;

	@Column(name = "audio_id", nullable = false)
	private Integer audioId;

	@Column(name = "file_name", nullable = false, length = 200)
	private String fileName;

	@Column(name = "file_path", nullable = false, length = 500)
	private String filePath;

	@Column(name = "file_format", nullable = false, length = 10)
	private String fileFormat;

	@Column(name = "file_created_at")
	private LocalDateTime fileCreatedAt;

	@Column(name = "file_size_bytes")
	private Long fileSizeBytes;

	@Column(name = "audio_transcript_consistent")
	private Boolean audioTranscriptConsistent;

	@Column(name = "verified_by_matric", length = 20)
	private String verifiedByMatric;

	@Column(name = "verified_by_name", length = 100)
	private String verifiedByName;

	@Column(name = "verified_at")
	private LocalDateTime verifiedAt;

	// --- GETTERS AND SETTERS ---
	public Integer getTranscriptId() {
		return transcriptId;
	}

	public void setTranscriptId(Integer transcriptId) {
		this.transcriptId = transcriptId;
	}

	public Integer getReelId() {
		return reelId;
	}

	public void setReelId(Integer reelId) {
		this.reelId = reelId;
	}

	public Integer getAudioId() {
		return audioId;
	}

	public void setAudioId(Integer audioId) {
		this.audioId = audioId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}

	public LocalDateTime getFileCreatedAt() { return fileCreatedAt; }
	public void setFileCreatedAt(LocalDateTime fileCreatedAt) { this.fileCreatedAt = fileCreatedAt; }

	public Long getFileSizeBytes() { return fileSizeBytes; }
	public void setFileSizeBytes(Long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }

	public Boolean getAudioTranscriptConsistent() { return audioTranscriptConsistent; }
	public void setAudioTranscriptConsistent(Boolean audioTranscriptConsistent) { this.audioTranscriptConsistent = audioTranscriptConsistent; }

	public String getVerifiedByMatric() { return verifiedByMatric; }
	public void setVerifiedByMatric(String verifiedByMatric) { this.verifiedByMatric = verifiedByMatric; }

	public String getVerifiedByName() { return verifiedByName; }
	public void setVerifiedByName(String verifiedByName) { this.verifiedByName = verifiedByName; }

	public LocalDateTime getVerifiedAt() { return verifiedAt; }
	public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
}