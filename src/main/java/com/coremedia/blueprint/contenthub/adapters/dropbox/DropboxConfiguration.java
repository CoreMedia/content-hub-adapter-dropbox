package com.coremedia.blueprint.contenthub.adapters.dropbox;

import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DropboxConfiguration {
  @Bean
  public ContentHubAdapterFactory dropboxContentHubAdapterFactory() {
    return new DropboxContentHubAdapterFactory();
  }
}
