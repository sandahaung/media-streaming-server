package com.lomatechnology.streaming.upload.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "uploaded_video")
public class UploadedVideo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, unique = true)
	private long id;

	@Column(name = "filename", nullable = false)
	private String filename;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "description", nullable = true)
	private String description;

	@Column(name = "uploaded_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date uploadedDate;

	@ToString.Exclude
	@Column(name = "filepath", nullable = false)
	private String filepath;

	@ToString.Exclude
	@Lob
	@Column(name = "content", columnDefinition = "LONGBLOB")
	private byte[] content;

	@ToString.Exclude
	@Lob
	@Column(name = "thumbnail", columnDefinition = "LONGBLOB")
	private byte[] thumbnail;
}
