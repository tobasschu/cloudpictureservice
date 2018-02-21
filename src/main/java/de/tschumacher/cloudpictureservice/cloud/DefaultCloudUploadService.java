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

import java.io.File;
import java.io.IOException;

import de.tschumacher.bucketservice.service.S3Service;
import de.tschumacher.cloudpictureservice.picture.CloudPicture;
import de.tschumacher.cloudpictureservice.picture.DeleteCloudPicture;
import de.tschumacher.cloudpictureservice.picture.models.PictureElement;
import de.tschumacher.utils.FilePathUtils;
import de.tschumacher.utils.IdentifierUtils;
import net.coobird.thumbnailator.Thumbnails.Builder;

public class DefaultCloudUploadService implements CloudUploadService {
  private final S3Service s3Service;

  public DefaultCloudUploadService(final S3Service s3Service) {
    this.s3Service = s3Service;
  }

  @Override
  public String createTemporaryUrl(final String key, int expirationMinutes) {
    if (this.s3Service.informationService().fileExists(key))
      return this.s3Service.downloadService().createPresignedUrl(key, expirationMinutes).toString();
    return null;
  }


  @Override
  public void uploadPicture(CloudPicture cloudPicture) throws IllegalStateException, IOException {
    for (final PictureElement element : cloudPicture.getDestElements()) {
      upload(cloudPicture.getSourceFile(), element);
    }
  }

  private void upload(File sourceFile, PictureElement element) throws IOException {
    final Builder<File> thumbBuilder = element.createThumbnailBuilder(sourceFile);
    final File thumbFile = new File(IdentifierUtils.createUniqueIdentifier(sourceFile.getName())
        + FilePathUtils.getFileExtension(sourceFile.getName()));
    thumbBuilder.toFile(thumbFile);
    this.s3Service.uploadService().uploadPublicFile(thumbFile, element.getUploadPath());
    thumbFile.delete();
  }

  @Override
  public void deletePicture(DeleteCloudPicture deleteCloudPicture) {
    for (final String path : deleteCloudPicture.getPicturePaths()) {
      this.s3Service.modificationService().deleteFile(path);
    }
  }

}
