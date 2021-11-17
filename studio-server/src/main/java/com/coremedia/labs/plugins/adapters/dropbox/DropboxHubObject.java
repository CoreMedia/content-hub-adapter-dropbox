package com.coremedia.labs.plugins.adapters.dropbox;


import com.coremedia.contenthub.api.BaseFileSystemHubObject;
import com.coremedia.contenthub.api.ContentHubObject;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.Metadata;
import edu.umd.cs.findbugs.annotations.NonNull;

abstract class DropboxHubObject extends BaseFileSystemHubObject implements ContentHubObject {

  private String pathDisplay;
  private DbxClientV2 client;

  DropboxHubObject(ContentHubObjectId hubId, Metadata metadata) {
    super(hubId, metadata != null? metadata.getName(): "root");
    if (metadata != null) {  //ROOT
      this.pathDisplay = metadata.getPathDisplay();
    }

  }

  @NonNull
  @Override
  public String getDisplayName() {
    return getName();
  }

  @NonNull
  String getPathDisplay() {
    return pathDisplay;
  }

  public DbxClientV2 getClient() {
    return client;
  }

  public void setClient(DbxClientV2 client) {
    this.client = client;
  }
}
