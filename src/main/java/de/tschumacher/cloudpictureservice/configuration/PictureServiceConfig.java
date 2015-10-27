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
package de.tschumacher.cloudpictureservice.configuration;

public class PictureServiceConfig {

	private final int expirationMinutes;
	private final String thumbnailPostfix;
	private final int thumbnailSize;
	private final float thumbnailQuality;
	private final String defaultPath;
	private final int pictureSize;
	private final float pictureQuality;
	private final String defaultUrl;

	public PictureServiceConfig(int expirationMinutes, String thumbnailPostfix,
			int thumbnailSize, float thumbnailQuality, String defaultPath,
			int pictureSize, float pictureQuality, String defaultUrl) {
		super();
		this.expirationMinutes = expirationMinutes;
		this.thumbnailPostfix = thumbnailPostfix;
		this.thumbnailSize = thumbnailSize;
		this.thumbnailQuality = thumbnailQuality;
		this.defaultPath = defaultPath;
		this.pictureSize = pictureSize;
		this.pictureQuality = pictureQuality;
		this.defaultUrl = defaultUrl;
	}

	public int getExpirationMinutes() {
		return expirationMinutes;
	}

	public String getThumbnailPostfix() {
		return thumbnailPostfix;
	}

	public int getThumbnailSize() {
		return thumbnailSize;
	}

	public float getThumbnailQuality() {
		return thumbnailQuality;
	}

	public String getDefaultPath() {
		return defaultPath;
	}

	public int getPictureSize() {
		return pictureSize;
	}

	public float getPictureQuality() {
		return pictureQuality;
	}

	public String getDefaultUrl() {
		return defaultUrl;
	}

}
