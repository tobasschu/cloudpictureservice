/*
 * Copyright 2015 Tobias Schumacher
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package de.tschumacher.cloudpictureservice.cloud;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import de.tschumacher.bucketservice.service.DefaultS3Service;
import de.tschumacher.bucketservice.service.S3Service;
import de.tschumacher.bucketservice.service.download.S3DownloadService;
import de.tschumacher.bucketservice.service.information.S3InformationService;
import de.tschumacher.bucketservice.service.upload.S3UploadService;
import de.tschumacher.cloudpictureservice.picture.CloudPicture;
import de.tschumacher.cloudpictureservice.picture.models.PictureElement;
import de.tschumacher.cloudpictureservice.picture.models.Size;

public class CloudUploadServiceTest {
  private CloudUploadService service = null;
  private S3Service s3Service = null;
  private S3DownloadService s3DownloadService = null;
  private S3InformationService s3InformationService = null;
  private S3UploadService s3UploadService = null;

  @Before
  public void setUp() {
    this.s3Service = Mockito.mock(DefaultS3Service.class);
    this.s3DownloadService = Mockito.mock(S3DownloadService.class);
    Mockito.when(this.s3Service.downloadService()).thenReturn(this.s3DownloadService);
    this.s3InformationService = Mockito.mock(S3InformationService.class);
    Mockito.when(this.s3Service.informationService()).thenReturn(this.s3InformationService);
    this.s3UploadService = Mockito.mock(S3UploadService.class);
    Mockito.when(this.s3Service.uploadService()).thenReturn(this.s3UploadService);
    this.service = new DefaultCloudUploadService(this.s3Service);
  }

  @After
  public void afterTest() {
    Mockito.verifyNoMoreInteractions(this.s3DownloadService);
    Mockito.verifyNoMoreInteractions(this.s3InformationService);
    Mockito.verifyNoMoreInteractions(this.s3UploadService);
    new File("src/test/resources/filname.png").delete();
    new File("src/test/resources/filname_tn.png").delete();
  }

  @Test
  public void convertToUrlTest() throws MalformedURLException {
    final String key = "key";
    final URL url = new URL("http://www.google.de");

    Mockito.when(this.s3DownloadService.createPresignedUrl(Matchers.anyString(), Matchers.anyInt()))
        .thenReturn(url);
    Mockito.when(this.s3InformationService.fileExists(Matchers.anyString())).thenReturn(true);

    final String createTemporaryUrl = this.service.createTemporaryUrl(key, 20);

    assertEquals(createTemporaryUrl, url.toString());

    Mockito.verify(this.s3DownloadService, Mockito.times(1))
        .createPresignedUrl(Matchers.anyString(), Matchers.anyInt());
    Mockito.verify(this.s3InformationService, Mockito.times(1)).fileExists(Matchers.anyString());
  }

  @Test
  public void uploadPicture() throws IllegalStateException, IOException {
    final File file = new File("src/test/resources/test.jpg");

    final CloudPicture cloudPicture = new CloudPicture(file);
    cloudPicture
        .addPictureElement(PictureElement.newBuilder().withUploadPath("upload/original").build());
    cloudPicture.addPictureElement(PictureElement.newBuilder().withUploadPath("upload/thumb")
        .withCrop(true).withQuality(0.5).withSize(Size.of(300, 200)).build());
    cloudPicture.addPictureElement(PictureElement.newBuilder().withUploadPath("upload/")
        .withCrop(true).withQuality(0.8).withSize(Size.of(1200, 800)).build());

    this.service.uploadPicture(cloudPicture);

    Mockito.verify(this.s3UploadService, Mockito.times(cloudPicture.getDestElements().size()))
        .uploadPublicFile(Matchers.any(File.class), Matchers.anyString());

  }
}
