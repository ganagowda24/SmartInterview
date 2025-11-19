package com.interview.platform.controller;

import com.interview.platform.service.SpeechToTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/speech")
public class SpeechController {

    @Autowired
    private SpeechToTextService speechService;

    @PostMapping("/transcribe")
    public String transcribeAudio(
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam("sessionId") Long sessionId,
            @RequestParam("questionId") Long questionId) throws Exception {

        // Save the audio locally
        String savedPath = speechService.saveAudioFile(audioFile, sessionId, questionId);

        // Get the transcription text
        String transcription = speechService.transcribeAudio(audioFile);

        return """
            ✅ Audio saved: %s
            🗣️ Transcription:
            %s
        """.formatted(savedPath, transcription);
    }
}
