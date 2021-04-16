package com.coremedia.labs.plugins.adapters.dropbox.server;


import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.ContentHubType;
import com.coremedia.contenthub.api.Folder;
import com.dropbox.core.v2.files.Metadata;
import edu.umd.cs.findbugs.annotations.NonNull;

class DropboxFolder extends DropboxHubObject implements Folder {

  private boolean isRoot = false;

  DropboxFolder(ContentHubObjectId id, Metadata metadata, String name) {
    super(id, metadata);
    if (metadata == null) {
      isRoot = true;
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
