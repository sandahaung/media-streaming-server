package com.lomatechnology.streaming.upload.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lomatechnology.streaming.upload.entity.UploadedVideo;
import com.lomatechnology.streaming.upload.service.UploadedVideoService;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/api/media")
public class StreamingController {
	
	private static final Logger logger = LoggerFactory.getLogger(StreamingController.class);
	
	@Value("${server.compression.mime-types}")
    private List<String> contentVideos;
	
	@Autowired
	private UploadedVideoService service;
	
	@PostMapping(
            value = "/upload/video",
            consumes = {
                    MediaType.MULTIPART_FORM_DATA_VALUE,
                    MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<String> uploadVideo(
    		@RequestParam("title") String title,
    		@RequestParam("description") String description,
            @RequestParam("video") @Validated @NotNull @NotEmpty MultipartFile file) throws IOException {
        String contentType = file.getContentType();

        if (!contentVideos.contains(contentType)) {
        	
            logger.info("wrong video type: {}", contentType);
            return badRequest().body("Wrong video type!");
        }

        UploadedVideo metaData = service.createFile(title, description, file);
        return ok(metaData.toString());
    }
}
