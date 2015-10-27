/*
   Copyright 2015 Tobias Schumacher

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package de.tschumacher.picturecloudservice.cloud;

import java.io.File;
import java.io.IOException;

import net.coobird.thumbnailator.Thumbnails;

import org.springframework.web.multipart.MultipartFile;

import de.tschumacher.picturecloudservice.cloud.s3.AmazonS3Service;
import de.tschumacher.picturecloudservice.configuration.PictureServiceConfig;
import de.tschumacher.utils.FilePathUtils;
import de.tschumacher.utils.FileUtils;

public class DefaultCloudUploadService implements CloudUploadService {
	private final AmazonS3Service s3Service;
	private final PictureServiceConfig config;

	public DefaultCloudUploadService(final AmazonS3Service s3Service,
			final PictureServiceConfig config) {
		this.s3Service = s3Service;
		this.config = config;
	}

	@Override
	public String createTemporaryUrl(final String key) {
		if (this.s3Service.fileExists(key))
			return this.s3Service.createPresignedUrl(key,
					this.config.getExpirationMinutes());
		return null;
	}

	@Override
	public void uploadPicture(final MultipartFile file,
			final String relativePath, final String relativeThumbnailPath)
			throws IllegalStateException, IOException {
		final File localFile = FileUtils.convertToFile(file);
		final File smallerFile = new File(file.getOriginalFilename());
		Thumbnails
				.of(localFile)
				.size(this.config.getPictureSize(),
						this.config.getPictureSize())
				.outputQuality(this.config.getPictureQuality())
				.toFile(smallerFile);

		this.s3Service.uploadFile(smallerFile, relativePath);

		generateThumbnail(localFile, relativeThumbnailPath,
				file.getOriginalFilename());
		localFile.delete();
		smallerFile.delete();
	}

	private void generateThumbnail(final File localFile,
			final String relativeThumbnailPath, final String filename)
			throws IOException {
		final File thumbnail = new File(FilePathUtils.createThumbnailPath(
				filename, this.config.getThumbnailPostfix()));
		Thumbnails
				.of(localFile)
				.size(this.config.getThumbnailSize(),
						this.config.getThumbnailSize())
				.outputQuality(this.config.getThumbnailQuality())
				.toFile(thumbnail);
		this.s3Service.uploadFile(thumbnail, relativeThumbnailPath);
		thumbnail.delete();
	}

	@Override
	public void deletePicture(final String url, final String thumbnailUrl) {
		this.s3Service.delete(url);
		this.s3Service.delete(thumbnailUrl);

	}

}
