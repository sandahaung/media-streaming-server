package com.lomatechnology.streaming.upload.service.impl;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.lomatechnology.streaming.upload.callback.VideoConversionCompleted;
import com.lomatechnology.streaming.upload.dao.UploadedVideoDao;
import com.lomatechnology.streaming.upload.entity.UploadedVideo;
import com.lomatechnology.streaming.upload.service.UploadedVideoConversionService;
import com.lomatechnology.streaming.upload.service.UploadedVideoService;
import com.lomatechnology.streaming.upload.util.CompressedVideoUtil;

@Service
public class UploadedVideoConversionServiceImpl implements UploadedVideoConversionService {

	@Autowired
	private UploadedVideoDao dao;
	
	@Autowired
	private CompressedVideoUtil compressedVideoUtil;
	
	@Autowired
	private UploadedVideoService uploadedVideoService;
	
	private static final Logger logger = LoggerFactory.getLogger(UploadedVideoConversionServiceImpl.class);

	@Scheduled(fixedRate = 10000)
	public void scheduleFixedRateTask() {
	    findConversionInDatabase();
	}
	
	//This method will not work if video conversion starts before midnight and finishes after midnight the next day 
	public void findConversionInDatabase() {
		List<UploadedVideo> unprocessedVideos = dao.findUnprocessedVideos();
		for (UploadedVideo unprocessedVideo : unprocessedVideos) {
			
			logger.info("video processing started");
			
			VideoConversionCompleted videoConversionCompleted = new VideoConversionCompleted() {
				@Override
				public void videoConversionCompleted() {
					logger.info("conversion is completed");
					try {
					unprocessedVideo.setThumbnail(uploadedVideoService.readFile("thumbnail", "png", unprocessedVideo.getUploadedDate(), unprocessedVideo.getId()));
					unprocessedVideo.setContent(uploadedVideoService.readFile("compressed", "mp4", unprocessedVideo.getUploadedDate(), unprocessedVideo.getId()));
					dao.saveChanges(unprocessedVideo);
					} catch (IOException e) {
						logger.info(e.toString());
						logger.info("error while reading video");
					}
				}
			};
			processThumbnail(unprocessedVideo);
			processVideo(unprocessedVideo, videoConversionCompleted);
		}
	}
	
	private void processVideo(UploadedVideo uploadedVideo, VideoConversionCompleted videoConversionCompleted) {
		try {
			compressedVideoUtil.convertVideoForStorage(uploadedVideo.getFilepath(), uploadedVideo.getId(), "mp4", videoConversionCompleted);
		} catch (IOException e) {
			logger.info(e.toString());
			logger.info("error while compressing video");
		}
	}
	
	private void processThumbnail(UploadedVideo uploadedVideo) {
		try {
			compressedVideoUtil.generateThumbnail(uploadedVideo.getFilepath(), uploadedVideo.getId());
		} catch (IOException e) {
			logger.info(e.toString());
			logger.info("error while generating thumbnail");
		}
	}

}
