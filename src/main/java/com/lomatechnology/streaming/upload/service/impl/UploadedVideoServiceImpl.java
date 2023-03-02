package com.lomatechnology.streaming.upload.service.impl;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
		 String dirPath = new StringBuilder(getHomeDirectory()).append(File.separator)
	                .append("Videos").append(File.separator)
	                .append(path).append(File.separator)
	                .append(uploadedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).toString();
		 
		 File dir = new File(dirPath);
	        if (!dir.exists()) {
	            dir.mkdirs();
	        }
		 
		String fileLocation = new StringBuilder(dirPath)
                .append(File.separator).append(path).append(id).append(".").append(type).toString();
		
		logger.info("reading file: " + fileLocation);
		
		return storageUtil.getFile(fileLocation);
    }
}
