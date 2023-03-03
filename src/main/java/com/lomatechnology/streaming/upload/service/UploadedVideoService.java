package com.lomatechnology.streaming.upload.service;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonObject;
import com.lomatechnology.streaming.upload.entity.UploadedVideo;

public interface UploadedVideoService {

	UploadedVideo createFile(String title, String description, MultipartFile file) throws IOException;

	byte[] readFile(String path, String type, Date uploadedDate, long id) throws IOException;

	JsonObject getAllVideos();

	Optional<UploadedVideo> findById(String id);
}
