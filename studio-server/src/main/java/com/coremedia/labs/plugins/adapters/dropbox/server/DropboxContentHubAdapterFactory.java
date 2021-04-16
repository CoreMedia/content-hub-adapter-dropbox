package com.coremedia.labs.plugins.adapters.dropbox.server;

import com.coremedia.contenthub.api.ContentHubAdapter;
import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import com.coremedia.contenthub.api.ContentHubMimeTypeService;
import com.coremedia.contenthub.api.ContentHubType;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Map;

/**
 *
 */
class DropboxContentHubAdapterFactory implements ContentHubAdapterFactory<DropboxContentHubSettings> {

  ContentHubMimeTypeService mimeTypeService;
  private final Map<ContentHubType, String> typeMapping;

  public DropboxContentHubAdapterFactory(ContentHubMimeTypeService mimeTypeService,
                                         Map<ContentHubType, String> typeMapping) {
    this.mimeTypeService = mimeTypeService;
    this.typeMapping = typeMapping;
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
    return new DropboxContentHubAdapter(settings, connectionId, mimeTypeService, typeMapping);
  }

}
