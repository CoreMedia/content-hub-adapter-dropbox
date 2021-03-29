package com.coremedia.labs.plugins.adapters.dropbox.server;

import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DropboxConfiguration {
  @Bean
  public ContentHubAdapterFactory<?> dropboxContentHubAdapterFactory() {
    return new DropboxContentHubAdapterFactory();
  }
}
