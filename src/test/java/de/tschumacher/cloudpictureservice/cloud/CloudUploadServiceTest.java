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

import de.tschumacher.bucketservice.AmazonS3Service;
import de.tschumacher.cloudpictureservice.picture.CloudPicture;
import de.tschumacher.cloudpictureservice.picture.models.PictureElement;
import de.tschumacher.cloudpictureservice.picture.models.Size;

public class CloudUploadServiceTest {
  private CloudUploadService service = null;
  private AmazonS3Service s3Service = null;

  @Before
  public void setUp() {
    this.s3Service = Mockito.mock(AmazonS3Service.class);
    this.service = new DefaultCloudUploadService(this.s3Service);
  }

  @After
  public void afterTest() {
    Mockito.verifyNoMoreInteractions(this.s3Service);
    new File("src/test/resources/filname.png").delete();
    new File("src/test/resources/filname_tn.png").delete();
  }

  @Test
  public void convertToUrlTest() throws MalformedURLException {
    final String key = "key";
    final URL url = new URL("http://www.google.de");
    Mockito.when(this.s3Service.createPresignedUrl(Matchers.anyString(), Matchers.anyInt()))
        .thenReturn(url);
    Mockito.when(this.s3Service.fileExists(Matchers.anyString())).thenReturn(true);

    final String createTemporaryUrl = this.service.createTemporaryUrl(key, 20);

    assertEquals(createTemporaryUrl, url.toString());

    Mockito.verify(this.s3Service, Mockito.times(1)).createPresignedUrl(Matchers.anyString(),
        Matchers.anyInt());
    Mockito.verify(this.s3Service, Mockito.times(1)).fileExists(Matchers.anyString());
  }

  @Test
  public void uploadPicture() throws IllegalStateException, IOException {
    final File file = new File("src/test/resources/test.jpg");

    final CloudPicture cloudPicture = new CloudPicture(file);
    cloudPicture.addPictureElement(PictureElement.newBuilder().withUploadPath("upload/original")
        .build());
    cloudPicture.addPictureElement(PictureElement.newBuilder().withUploadPath("upload/thumb")
        .withCrop(true).withQuality(0.5).withSize(Size.of(300, 200)).build());
    cloudPicture.addPictureElement(PictureElement.newBuilder().withUploadPath("upload/")
        .withCrop(true).withQuality(0.8).withSize(Size.of(1200, 800)).build());

    this.service.uploadPicture(cloudPicture);

    Mockito.verify(this.s3Service, Mockito.times(cloudPicture.getDestElements().size()))
        .uploadPublicFile(Matchers.any(File.class), Matchers.anyString());

  }
}
