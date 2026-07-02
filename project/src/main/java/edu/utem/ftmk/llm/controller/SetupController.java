package edu.utem.ftmk.llm.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.utem.ftmk.llm.entity.AudioFile;
import edu.utem.ftmk.llm.entity.Reel;
import edu.utem.ftmk.llm.entity.Transcript;
import edu.utem.ftmk.llm.repository.AudioFileRepository;
import edu.utem.ftmk.llm.repository.ReelRepository;
import edu.utem.ftmk.llm.repository.TranscriptRepository;

@RestController
public class SetupController {

    @Autowired
    private TranscriptRepository transcriptRepo;

    @Autowired
    private AudioFileRepository audioFileRepo;

    @Autowired
    private ReelRepository reelRepo;

    @GetMapping("/api/setup/scan-all")
    public String scanAll() {
        String res1 = scanAndInsertReels();
        String res2 = scanAndInsertAudioFiles();
        String res3 = scanAndInsertTranscripts();
        return "<h3>Scan Complete!</h3>" +
               "<p><b>Reels:</b> " + res1 + "</p>" +
               "<p><b>Audio:</b> " + res2 + "</p>" +
               "<p><b>Transcripts:</b> " + res3 + "</p>";
    }

    /**
     * Endpoint 1: Scans and inserts .txt Transcript files
     * Note: Linked to Reel ID 1 to satisfy DB constraints
     */
    @GetMapping("/api/setup/scan")
    public String scanAndInsertTranscripts() {
        String folderPath = "D:\\DAD_Project\\nutritional-llm-service\\transcriptions"; 
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) return "Error: Folder not found.";

        List<Transcript> newTranscripts = new ArrayList<>();
        List<Reel> allReels = reelRepo.findAll();
        List<AudioFile> allAudios = audioFileRepo.findAll();

        int count = 0;
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                String fileName = file.getName();
                String reelIdInstagram = null;
                try {
                    List<String> lines = Files.readAllLines(file.toPath());
                    String sourceUrl = "";
                    for(String line : lines) {
                        if(line.startsWith("Source URL")) {
                            sourceUrl = line.substring(line.indexOf("http")).trim();
                            break;
                        }
                    }
                    if(!sourceUrl.isEmpty()) {
                        String[] urlParts = sourceUrl.split("/reel/");
                        reelIdInstagram = urlParts.length > 1 ? urlParts[1].split("/")[0].split("\\?")[0] : "unknown";
                    }
                } catch (Exception e) {
                    System.out.println("Failed to read: " + file.getName());
                }

                if (reelIdInstagram == null || reelIdInstagram.equals("unknown")) {
                    System.out.println("Skipping " + fileName + ": Could not extract Instagram ID.");
                    continue;
                }

                final String finalReelIdInstagram = reelIdInstagram;
                
                // 1. Find matching Reel by extracted Instagram ID
                Integer matchedReelId = allReels.stream()
                    .filter(r -> finalReelIdInstagram.equals(r.getReelIdInstagram()))
                    .map(Reel::getReelId)
                    .findFirst().orElse(null);

                // 2. Find matching Audio by checking if the Audio fileName matches the extracted Instagram ID
                Integer matchedAudioId = allAudios.stream()
                    .filter(a -> finalReelIdInstagram.equals(a.getFileName().split("\\.")[0]))
                    .map(AudioFile::getAudioId)
                    .findFirst().orElse(null);

                if (matchedReelId != null && matchedAudioId != null) {
                    // Check if already exists in DB
                    boolean exists = transcriptRepo.findAll().stream()
                        .anyMatch(t -> fileName.equals(t.getFileName()));
                    
                    if (exists) {
                        System.out.println("Skipping " + fileName + ": Already exists in DB.");
                        continue;
                    }

                    Transcript t = new Transcript();
                    t.setReelId(matchedReelId);
                    t.setAudioId(matchedAudioId);
                    
                    t.setFileName(fileName);
                    t.setFilePath(file.getAbsolutePath().replace("\\", "/"));
                    t.setFileFormat("txt");

                    try {
                        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                        LocalDateTime creationTime = LocalDateTime.ofInstant(attr.creationTime().toInstant(), ZoneId.systemDefault());
                        t.setFileCreatedAt(creationTime);
                    } catch (IOException e) {
                        t.setFileCreatedAt(LocalDateTime.now());
                    }

                    t.setFileSizeBytes(file.length());
                    t.setAudioTranscriptConsistent(true);
                    
                    // Audit Trail
                    t.setVerifiedByMatric("B032410510");
                    t.setVerifiedByName("MUHAMMAD IRFAN BIN MOHD ZAIN");
                    t.setVerifiedAt(LocalDateTime.now());
                    
                    newTranscripts.add(t);
                    count++;
                } else {
                    System.out.println("Skipping " + fileName + ": No match (Reel: " + matchedReelId + ", Audio: " + matchedAudioId + ")");
                }
            }
        }
        transcriptRepo.saveAll(newTranscripts);
        return "Success! Linked " + count + " transcripts.";
    }

    /**
     * Endpoint 2: Scans and inserts Audio files
     * Note: Linked to Reel ID 1 to satisfy DB constraints
     */
    @GetMapping("/api/setup/scan-audio")
    public String scanAndInsertAudioFiles() {
        String folderPath = "D:\\DAD_Project\\nutritional-llm-service\\audio"; 
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) return "Error: Folder not found.";

        List<AudioFile> newAudioFiles = new ArrayList<>();
        List<Reel> allReels = reelRepo.findAll(); 

        int count = 0;
        for (File file : listOfFiles) {
            if (file.isFile() && (file.getName().endsWith(".mp3") || file.getName().endsWith(".wav"))) {
                String fileName = file.getName();
                
                try {
                    // Read physical file metadata
                    BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    LocalDateTime creationTime = LocalDateTime.ofInstant(
                        attr.creationTime().toInstant(), 
                        ZoneId.systemDefault()
                    );

                    // Link logic: match filename to Reel ID
                    Integer matchedReelId = allReels.stream()
                        .filter(r -> fileName.contains(r.getReelIdInstagram()))
                        .map(Reel::getReelId)
                        .findFirst()
                        .orElse(null); 

                    if (matchedReelId != null) {
                        // Check if already exists in DB
                        boolean exists = audioFileRepo.findAll().stream()
                            .anyMatch(a -> fileName.equals(a.getFileName()));
                        
                        if (exists) {
                            System.out.println("Skipping " + fileName + ": Already exists in DB.");
                            continue;
                        }

                        AudioFile a = new AudioFile();
                        a.setReelId(matchedReelId);
                        a.setFileName(fileName);
                        a.setFilePath(file.getAbsolutePath().replace("\\", "/"));
                        a.setFileCreatedAt(creationTime);
                        a.setFileFormat(fileName.substring(fileName.lastIndexOf(".") + 1));
                        a.setFileSizeBytes(file.length());
                        a.setReelAudioConsistent(true); 
                        
                        // Audit Trail
                        a.setVerifiedByMatric("B032410510");
                        a.setVerifiedByName("MUHAMMAD IRFAN");
                        a.setVerifiedAt(LocalDateTime.now());
                        
                        newAudioFiles.add(a);
                        count++;
                    } else {
                        System.out.println("No match found for file: " + fileName);
                    }
                } catch (IOException e) {
                    System.err.println("Could not read attributes for file: " + fileName);
                }
            }
        }
        audioFileRepo.saveAll(newAudioFiles);
        return "Scan complete. Successfully linked and inserted " + count + " audio files.";
    }

    /**
     * Endpoint 3: Extracts Reel URLs and IDs
     */
    @GetMapping("/api/setup/scan-reels")
    public String scanAndInsertReels() {
        String folderPath = "D:\\DAD_Project\\nutritional-llm-service\\transcriptions"; 
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) return "Error: Folder not found.";

        List<Reel> newReels = new ArrayList<>();
        int count = 0;

        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                try {
                    List<String> lines = Files.readAllLines(file.toPath());
                    String sourceUrl = "";
                    for(String line : lines) {
                        if(line.startsWith("Source URL")) {
                            sourceUrl = line.substring(line.indexOf("http")).trim();
                            break;
                        }
                    }

                    if(!sourceUrl.isEmpty()) {
                        String[] urlParts = sourceUrl.split("/reel/");
                        String extractedId = urlParts.length > 1 ? urlParts[1].split("/")[0].split("\\?")[0] : "unknown";
                        
                        // Check if already exists in DB
                        boolean exists = reelRepo.findAll().stream()
                            .anyMatch(r -> extractedId.equals(r.getReelIdInstagram()));
                            
                        if (exists) {
                            continue;
                        }

                        Reel reel = new Reel();
                        reel.setInfluencerId(1); // Set to 1 as requested
                        reel.setReelUrl(sourceUrl);
                        reel.setReelIdInstagram(extractedId);
                        
                        reel.setIdentifiedByName("MUHAMMAD IRFAN BIN MOHD ZAIN");
                        reel.setIdentifiedByMatric("B032410510"); 
                        reel.setIdentifiedDate(LocalDate.now());
                        newReels.add(reel);
                        count++;
                    }
                } catch (Exception e) {
                    System.out.println("Failed: " + file.getName());
                }
            }
        }
        reelRepo.saveAll(newReels);
        return "Success! Inserted " + count + " reels.";
    }
}