package com.lomatechnology.streaming.upload.util;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.lomatechnology.streaming.upload.callback.VideoConversionCompleted;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CompressedVideoUtil {

	private FFmpeg ffmpeg;
	private FFprobe ffprobe;

	private static final Logger logger = LoggerFactory.getLogger(CompressedVideoUtil.class);

	public String getHomeDirectory() {
		return System.getProperty("user.home");
	}

	public CompressedVideoUtil() throws IOException {
		String ffmpegDir = "D:\\binaries\\ffmpeg-2023-02-27-git-891ed24f77-full_build\\bin\\ffmpeg.exe";
		String ffprobeDir = "D:\\binaries\\ffmpeg-2023-02-27-git-891ed24f77-full_build\\bin\\ffprobe.exe";
		if (StringUtils.hasLength(ffmpegDir))
			ffmpeg = new FFmpeg(ffmpegDir);
		ffprobe = new FFprobe();
		if (StringUtils.hasLength(ffprobeDir))
			ffprobe = new FFprobe(ffprobeDir);

	}

	public String generateThumbnail(String filename, long id) throws IOException {
		String dirPath = new StringBuilder(getHomeDirectory()).append(File.separator).append("Videos")
				.append(File.separator).append("thumbnail").append(File.separator).append(LocalDate.now()).toString();

		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String outputLocation = new StringBuilder(dirPath).append(File.separator).append("thumbnail").append(id)
				.append(".").append("png").toString();
		FFmpegBuilder builder = new FFmpegBuilder();
		FFmpegProbeResult input = ffprobe.probe(filename);
		builder.setInput(input).addOutput(outputLocation).setFrames(1)
				.setVideoFilter("select='gte(n\\,10)',scale=200:-1").done();

		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
		executor.createJob(builder).run();
		return outputLocation;
	}

	public String convertVideo(String fileName, String format) throws IOException {
		String dirPath = new StringBuilder(getHomeDirectory()).append(File.separator).append("Videos")
				.append(File.separator).append("compressed").append(File.separator).append(LocalDate.now()).toString();

		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String outputLocation = new StringBuilder(dirPath).append(File.separator).append(UUID.randomUUID().toString())
				.append(".").append(format).toString();
		FFmpegProbeResult input = ffprobe.probe(fileName);
		FFmpegBuilder builder = new FFmpegBuilder().setInput(input).overrideOutputFiles(true)

				.addOutput(outputLocation).setFormat(format).disableSubtitle()

//                config audio
				.setAudioChannels(1).setAudioCodec("aac").setAudioSampleRate(48_000).setAudioBitRate(32_768)

//                config video
				.setVideoCodec("libx264").setVideoFrameRate(24, 1).setVideoResolution(320, 240)

				.setStrict(FFmpegBuilder.Strict.EXPERIMENTAL).done();

		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
		executor.createJob(builder, new ProgressListener() {

			final double duration_ns = input.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

			@Override
			public void progress(Progress progress) {
				double percentage = progress.out_time_ns / duration_ns;

				if (progress.status.equals(Progress.Status.END)) {
					logger.info("filename: {} completed!", input.getFormat().filename);
				} else {
					logger.info("filename: {} -> {} status: {} time: {} ms", input.getFormat().filename,
							String.format("[%.0f%%]", (percentage * 100)), progress.status,
							FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS));
				}
			}
		}).run();
		return outputLocation;
	}

	public String convertVideoForStorage(String filename, long id, String format,
			VideoConversionCompleted videoConversionCompleted) throws IOException {
		String dirPath = new StringBuilder(getHomeDirectory()).append(File.separator).append("Videos")
				.append(File.separator).append("compressed").append(File.separator).append(LocalDate.now()).toString();

		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String outputLocation = new StringBuilder(dirPath).append(File.separator).append("compressed").append(id)
				.append(".").append(format).toString();
		FFmpegProbeResult input = ffprobe.probe(filename);
		FFmpegBuilder builder = new FFmpegBuilder().setInput(input).overrideOutputFiles(true)

				.addOutput(outputLocation).setFormat(format).disableSubtitle()

//                config audio
				.setAudioChannels(1).setAudioCodec("aac").setAudioSampleRate(48_000).setAudioBitRate(32_768)

//                config video
				.setVideoCodec("libx264").setVideoFrameRate(24, 1).setVideoResolution(320, 240)

				.setStrict(FFmpegBuilder.Strict.EXPERIMENTAL).done();

		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
		executor.createJob(builder, new ProgressListener() {

			final double duration_ns = input.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

			@Override
			public void progress(Progress progress) {
				double percentage = progress.out_time_ns / duration_ns;

				if (progress.status.equals(Progress.Status.END)) {
					logger.info("filename: {} completed!", input.getFormat().filename);
					videoConversionCompleted.videoConversionCompleted();
				} else {
					logger.info("filename: {} -> {} status: {} time: {} ms", input.getFormat().filename,
							String.format("[%.0f%%]", (percentage * 100)), progress.status,
							FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS));
				}
			}
		}).run();
		return outputLocation;
	}
}