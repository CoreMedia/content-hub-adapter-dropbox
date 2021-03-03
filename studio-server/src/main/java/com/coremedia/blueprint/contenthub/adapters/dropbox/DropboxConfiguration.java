package com.coremedia.blueprint.contenthub.adapters.dropbox;

import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import com.coremedia.contenthub.api.ContentHubMimeTypeService;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DropboxConfiguration {
  @Bean
  public ContentHubAdapterFactory dropboxContentHubAdapterFactory(@NonNull ContentHubMimeTypeService mimeTypeService) {
    return new DropboxContentHubAdapterFactory(mimeTypeService);
  }
}
