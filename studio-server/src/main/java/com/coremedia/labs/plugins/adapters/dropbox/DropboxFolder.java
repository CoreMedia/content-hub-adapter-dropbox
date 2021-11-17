package com.coremedia.labs.plugins.adapters.dropbox;


import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.ContentHubType;
import com.coremedia.contenthub.api.Folder;
import com.dropbox.core.v2.files.Metadata;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.commons.lang3.StringUtils;

class DropboxFolder extends DropboxHubObject implements Folder {

  private boolean isRoot = false;
  private String name;

  DropboxFolder(ContentHubObjectId id, Metadata metadata, String name) {
    super(id, metadata);
    if (metadata == null) {
      isRoot = true;
    }

    this.name = name;
  }

  @Override
  public String getName() {
    if (StringUtils.isNotBlank(name)) {
      return name;
    } else {
      return super.getName();
    }
  }

  @NonNull
  @Override
  public ContentHubType getContentHubType() {
    return new ContentHubType("dbx_folder");
  }

  public boolean isRoot() {
    return isRoot;
  }
}
