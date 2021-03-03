package com.coremedia.blueprint.contenthub.adapters.dropbox;

import com.coremedia.contenthub.api.ContentHubAdapter;
import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import com.coremedia.contenthub.api.ContentHubMimeTypeService;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 *
 */
class DropboxContentHubAdapterFactory implements ContentHubAdapterFactory<DropboxContentHubSettings> {

  ContentHubMimeTypeService mimeTypeService;

  public DropboxContentHubAdapterFactory(ContentHubMimeTypeService mimeTypeService) {
    this.mimeTypeService = mimeTypeService;
  }

  @Override
  @NonNull
  public String getId() {
    return "dropbox";
  }

  @NonNull
  @Override
  public ContentHubAdapter createAdapter(@NonNull DropboxContentHubSettings settings,
                                         @NonNull String connectionId) {
    return new DropboxContentHubAdapter(settings, connectionId, mimeTypeService);
  }

}
