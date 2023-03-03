package com.lomatechnology.streaming.upload.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lomatechnology.streaming.upload.dao.UploadedVideoDao;
import com.lomatechnology.streaming.upload.entity.UploadedVideo;
import com.lomatechnology.streaming.upload.service.UploadedVideoService;
import com.lomatechnology.streaming.upload.util.FileStorageUtil;

@Service
@Transactional(readOnly = true)
public class UploadedVideoServiceImpl implements UploadedVideoService {

	@Autowired
	private UploadedVideoDao dao;

	@Autowired
	private FileStorageUtil storageUtil;

	private static final Logger logger = LoggerFactory.getLogger(UploadedVideoServiceImpl.class);

	public String getHomeDirectory() {
		return System.getProperty("user.home");
	}

	public Optional<UploadedVideo> findById(String id) {
		return dao.findById(id);
	}

	@Transactional
	public UploadedVideo createFile(String title, String description, MultipartFile file) throws IOException {
		UploadedVideo video = new UploadedVideo();
		video.setFilename(org.apache.commons.io.FilenameUtils.getName(file.getOriginalFilename()));
		video.setTitle(title);
		video.setDescription(description);
		video.setUploadedDate(new Date());
		String path = storageUtil.createFile(file);
		video.setFilepath(path);
		return dao.upload(video);
	}

	public byte[] readFile(String path, String type, Date uploadedDate, long id) throws IOException {
		String dirPath = new StringBuilder(getHomeDirectory()).append(File.separator).append("Videos")
				.append(File.separator).append(path).append(File.separator)
				.append(uploadedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).toString();

		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String fileLocation = new StringBuilder(dirPath).append(File.separator).append(path).append(id).append(".")
				.append(type).toString();

		logger.info("reading file: " + fileLocation);

		return storageUtil.getFile(fileLocation);
	}

	@Override
	public JsonObject getAllVideos() {
		JsonObject jsonObject = new JsonObject();
		int count = 0;
		JsonArray videos = new JsonArray();
		for (UploadedVideo video : dao.getAllVideos()) {
			JsonObject jsonObjectVideo = new JsonObject();
			jsonObjectVideo.addProperty("id", video.getId());
			jsonObjectVideo.addProperty("filename", video.getFilename());
			jsonObjectVideo.addProperty("title", video.getTitle());
			jsonObjectVideo.addProperty("description", video.getDescription());
			DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
			String strDate = dateFormat.format(video.getUploadedDate());
			jsonObjectVideo.addProperty("uploadDate", strDate);
			videos.add(jsonObjectVideo);
			count++;
		}
		jsonObject.addProperty("total", count);
		jsonObject.add("videos", videos);
		return jsonObject;
	}
}
