package com.coremedia.labs.plugins.adapters.dropbox;


/**
 * Interface that marks the settings that are needed for a connection to Dropbox
 */
interface DropboxContentHubSettings {

  /**
   * @return the name
   */
  String getDisplayName();

  /**
   * @return the Proxy Host
   */
  String getProxyHost();

  /**
   * @return a value of {@link java.net.Proxy}#type
   */
  String getProxyType();

  /**
   * @return a proxy port for your Dropbox Connection
   */
  Integer getProxyPort();

  //--------------------# Dropbox settings #--------------------//

  /**
   * @return a access token for your Dropbox Connection
   */
  String getAccessToken();

}
