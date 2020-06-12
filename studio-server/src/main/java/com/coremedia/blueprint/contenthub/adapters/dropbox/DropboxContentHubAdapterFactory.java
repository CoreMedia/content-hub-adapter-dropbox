package com.coremedia.blueprint.contenthub.adapters.dropbox;

import com.coremedia.contenthub.api.ContentHubAdapter;
import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 *
 */
class DropboxContentHubAdapterFactory implements ContentHubAdapterFactory<DropboxContentHubSettings> {

  @Override
  @NonNull
  public String getId() {
    return "dropbox";
  }

  @NonNull
  @Override
  public ContentHubAdapter createAdapter(@NonNull DropboxContentHubSettings settings,
                                         @NonNull String connectionId) {
    return new DropboxContentHubAdapter(settings, connectionId);
  }

}
