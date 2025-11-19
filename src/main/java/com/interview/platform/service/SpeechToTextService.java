package com.interview.platform.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.Base64;
import org.json.JSONObject;

@Service
public class SpeechToTextService {

    // Load API key from application.properties
    @Value("${assemblyai.api.key:}")
    private String apiKey;

    private static final String UPLOAD_URL = "https://api.assemblyai.com/v2/upload";
    private static final String TRANSCRIPT_URL = "https://api.assemblyai.com/v2/transcript";

    /**
     * Convert audio file to text using AssemblyAI API
     */
    public String transcribeAudio(MultipartFile audioFile) throws Exception {

        // If no API key provided, use mock transcription
        if (apiKey == null || apiKey.trim().isEmpty()) {
            System.out.println("⚠️ AssemblyAI API key not configured!");
            System.out.println("Falling back to mock transcription for demo purposes.");
            return mockTranscription(audioFile);
        }

        // STEP 1: Upload audio file to AssemblyAI
        String uploadUrl = uploadAudio(audioFile);

        // STEP 2: Request transcription
        String transcriptId = requestTranscription(uploadUrl);

        // STEP 3: Poll for result
        return getTranscriptionResult(transcriptId);
    }

    /**
     * Upload the audio file to AssemblyAI and return its upload_url
     */
    private String uploadAudio(MultipartFile audioFile) throws Exception {
        HttpURLConnection uploadConn = (HttpURLConnection) new URL(UPLOAD_URL).openConnection();
        uploadConn.setRequestMethod("POST");
        uploadConn.setRequestProperty("Authorization", apiKey);
        uploadConn.setDoOutput(true);

        try (OutputStream os = uploadConn.getOutputStream()) {
            os.write(audioFile.getBytes());
        }

        BufferedReader uploadReader = new BufferedReader(new InputStreamReader(uploadConn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = uploadReader.readLine()) != null) {
            response.append(line);
        }
        uploadReader.close();

        JSONObject uploadJson = new JSONObject(response.toString());
        return uploadJson.getString("upload_url");
    }

    /**
     * Create a transcription request and return the transcript ID
     */
    private String requestTranscription(String audioUrl) throws Exception {
        JSONObject requestJson = new JSONObject();
        requestJson.put("audio_url", audioUrl);
        requestJson.put("language_code", "en_us");
        requestJson.put("punctuate", true);

        HttpURLConnection conn = (HttpURLConnection) new URL(TRANSCRIPT_URL).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", apiKey);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestJson.toString().getBytes());
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONObject responseJson = new JSONObject(response.toString());
        return responseJson.getString("id");
    }

    /**
     * Poll AssemblyAI until the transcription is complete
     */
    private String getTranscriptionResult(String transcriptId) throws Exception {
        while (true) {
            Thread.sleep(3000); // wait 3 seconds between polls
            URL url = new URL(TRANSCRIPT_URL + "/" + transcriptId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", apiKey);

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject json = new JSONObject(response.toString());
            String status = json.getString("status");

            if (status.equals("completed")) {
                return json.getString("text");
            } else if (status.equals("error")) {
                throw new Exception("❌ Transcription failed: " + json.getString("error"));
            }
        }
    }

    /**
     * Mock transcription (used when API key missing)
     */
    private String mockTranscription(MultipartFile audioFile) {
        return "I am a passionate software developer with experience in Java, Spring Boot, and full stack development. "
                + "I have worked on several projects including web and enterprise applications. "
                + "I am excited about this opportunity and believe I would be a great fit for your team.";
    }

    /**
     * Save uploaded audio file to disk
     */
    public String saveAudioFile(MultipartFile audioFile, Long sessionId, Long questionId) throws IOException {
        String uploadDir = "uploads/audio/";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = "session_" + sessionId + "_question_" + questionId + "_"
                + System.currentTimeMillis() + ".wav";
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(audioFile.getInputStream(), filePath);

        return uploadDir + fileName;
    }
}
