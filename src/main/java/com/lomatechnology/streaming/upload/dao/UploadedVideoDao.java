package com.lomatechnology.streaming.upload.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.lomatechnology.streaming.upload.entity.UploadedVideo;
import com.lomatechnology.streaming.upload.repository.UploadedVideoRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class UploadedVideoDao {
	@Autowired
    private UploadedVideoRepository repository;

	public List<UploadedVideo> findUnprocessedVideos() {
        return repository.findUnprocessedVideos();
    }
	
    public Optional<UploadedVideo> findById(String id) {
        return repository.findById(id);
    }

    public UploadedVideo upload(UploadedVideo value) {
        return repository.save(value);
    }
    
    public UploadedVideo saveChanges(UploadedVideo value) {
        return repository.save(value);
    }
}
