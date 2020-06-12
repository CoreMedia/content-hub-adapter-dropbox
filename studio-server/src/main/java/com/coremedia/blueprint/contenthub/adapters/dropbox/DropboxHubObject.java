package com.coremedia.blueprint.contenthub.adapters.dropbox;


import com.coremedia.contenthub.api.ContentHubObject;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.MediaMetadata;
import com.dropbox.core.v2.files.Metadata;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

abstract class DropboxHubObject implements ContentHubObject {

  private ContentHubObjectId hubId;
  private String name;
  private String pathDisplay;
  private DbxClientV2 client;

  DropboxHubObject(ContentHubObjectId hubId, Metadata metadata) {
    this.hubId = hubId;
    if(metadata != null) {  //ROOT
      this.name = metadata.getName();
      this.pathDisplay = metadata.getPathDisplay();
    }

  }

  @NonNull
  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @NonNull
  @Override
  public String getDisplayName() {
    return getName();
  }

  @NonNull
  @Override
  public ContentHubObjectId getId() {
    return hubId;
  }

  @NonNull
  String getPathDisplay() {
    return pathDisplay;
  }

  public void setClient(DbxClientV2 client) {
    this.client = client;
  }

  public DbxClientV2 getClient(){
    return client;
  }
}