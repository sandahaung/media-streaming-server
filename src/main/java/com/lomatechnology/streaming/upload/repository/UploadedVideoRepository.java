package com.lomatechnology.streaming.upload.repository;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.lomatechnology.streaming.upload.entity.UploadedVideo;

public interface UploadedVideoRepository extends CrudRepository<UploadedVideo, String> {

    @Query("select u from UploadedVideo u where u.content is null")
    List<UploadedVideo> findUnprocessedVideos();
    
}
