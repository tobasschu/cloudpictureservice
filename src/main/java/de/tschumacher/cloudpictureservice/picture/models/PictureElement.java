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
package de.tschumacher.cloudpictureservice.picture.models;

import java.io.File;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;



public class PictureElement {
  private String uploadPath;
  private Size size;
  private Double quality;
  private boolean crop;

  public String getUploadPath() {
    return uploadPath;
  }

  public Size getSize() {
    return size;
  }

  public Double getQuality() {
    return quality;
  }

  public boolean shouldCrop() {
    return crop;
  }

  private PictureElement(Builder builder) {
    this.uploadPath = builder.uploadPath;
    this.size = builder.size;
    this.quality = builder.quality;
    this.crop = builder.crop;
  }


  public static Builder newBuilder() {
    return new Builder();
  }


  public static class Builder {

    private String uploadPath;
    private Size size;
    private Double quality;
    private boolean crop;

    public Builder withUploadPath(String uploadPath) {
      this.uploadPath = uploadPath;
      return this;
    }

    public Builder withSize(Size size) {
      this.size = size;
      return this;
    }

    public Builder withQuality(Double quality) {
      this.quality = quality;
      return this;
    }

    public Builder withCrop(boolean crop) {
      this.crop = crop;
      return this;
    }

    public PictureElement build() {
      return new PictureElement(this);
    }
  }

  public net.coobird.thumbnailator.Thumbnails.Builder<File> createThumbnailBuilder(File sourceFile) {
    net.coobird.thumbnailator.Thumbnails.Builder<File> thumbBuilder = Thumbnails.of(sourceFile);
    if (this.getSize() != null) {
      thumbBuilder.size(this.getSize().getWidth(), this.getSize().getHeight());
    } else {
      thumbBuilder.scale(1.0);
    }

    if (this.getQuality() != null) {
      thumbBuilder.outputQuality(this.getQuality());
    }

    if (this.shouldCrop()) {
      thumbBuilder.crop(Positions.CENTER);
    }

    return thumbBuilder;
  }



}
