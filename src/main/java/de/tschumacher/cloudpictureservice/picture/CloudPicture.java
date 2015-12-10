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
package de.tschumacher.cloudpictureservice.picture;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.tschumacher.cloudpictureservice.picture.models.PictureElement;

public class CloudPicture {
  private File sourceFile;
  private List<PictureElement> destElements;

  public CloudPicture(File sourceFile) {
    super();
    this.sourceFile = sourceFile;
    this.destElements = new ArrayList<PictureElement>();
  }

  public File getSourceFile() {
    return this.sourceFile;
  }

  public List<PictureElement> getDestElements() {
    return Collections.unmodifiableList(this.destElements);
  }

  public void addPictureElement(PictureElement element) {
    this.destElements.add(element);
  }

}
