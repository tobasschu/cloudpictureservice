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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import de.tschumacher.picturecloudservice.cloud.s3.AmazonS3Service;
import de.tschumacher.picturecloudservice.configuration.PictureServiceConfig;

public class CloudUploadServiceTest {
	private CloudUploadService service = null;
	private AmazonS3Service s3Service = null;
	private PictureServiceConfig config = null;

	@Before
	public void setUp() {
		this.config = new PictureServiceConfig(1, "_tn", 500, 0.9f, "path/",
				1000, 0.9f, "url");
		this.s3Service = Mockito.mock(AmazonS3Service.class);
		this.service = new DefaultCloudUploadService(this.s3Service,
				this.config);
	}

	@After
	public void afterTest() {
		Mockito.verifyNoMoreInteractions(this.s3Service);
		new File("src/test/resources/filname.png").delete();
		new File("src/test/resources/filname_tn.png").delete();
	}

	@Test
	public void convertToUrlTest() {
		final String key = "key";
		final String url = "url";
		Mockito.when(
				this.s3Service.createPresignedUrl(Matchers.anyString(),
						Matchers.anyInt())).thenReturn(url);
		Mockito.when(this.s3Service.fileExists(Matchers.anyString()))
				.thenReturn(true);

		final String createTemporaryUrl = this.service.createTemporaryUrl(key);

		assertEquals(createTemporaryUrl, url);

		Mockito.verify(this.s3Service, Mockito.times(1)).createPresignedUrl(
				Matchers.anyString(), Matchers.anyInt());
		Mockito.verify(this.s3Service, Mockito.times(1)).fileExists(
				Matchers.anyString());
	}

	@Test
	public void uploadPicture() throws IllegalStateException, IOException {
		final FileInputStream inputStream = new FileInputStream(
				"src/test/resources/test.jpg");
		final String filename = "src/test/resources/filname.png";
		final MultipartFile file = new MockMultipartFile(filename, filename,
				"contentype", inputStream);

		final String relativePath = "relativePath";
		final String relativeThumbnailPath = "relativeThumbnailPath";

		this.service.uploadPicture(file, relativePath, relativeThumbnailPath);

		Mockito.verify(this.s3Service, Mockito.times(2)).uploadFile(
				Matchers.any(File.class), Matchers.anyString());

	}
}
