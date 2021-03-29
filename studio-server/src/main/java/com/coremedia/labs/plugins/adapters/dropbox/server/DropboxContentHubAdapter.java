package com.coremedia.labs.plugins.adapters.dropbox.server;

import com.coremedia.contenthub.api.ContentHubAdapter;
import com.coremedia.contenthub.api.ContentHubContext;
import com.coremedia.contenthub.api.ContentHubObject;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.ContentHubTransformer;
import com.coremedia.contenthub.api.Folder;
import com.coremedia.contenthub.api.GetChildrenResult;
import com.coremedia.contenthub.api.Item;
import com.coremedia.contenthub.api.exception.ContentHubException;
import com.coremedia.contenthub.api.pagination.PaginationRequest;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.StandardHttpRequestor;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderBuilder;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;


class DropboxContentHubAdapter implements ContentHubAdapter {
  private static final Logger LOGGER = LoggerFactory.getLogger(DropboxContentHubAdapter.class);

  private final DropboxContentHubSettings settings;
  private final String connectionId;

  private final DbxClientV2 client;

  DropboxContentHubAdapter(@NonNull DropboxContentHubSettings settings, @NonNull String connectionId) {
    this.settings = settings;
    this.connectionId = connectionId;

    String accessToken = settings.getAccessToken();
    String displayName = settings.getDisplayName();

    if (accessToken == null || accessToken.trim().length() == 0) {
      String msg = "No accessToken configured for Dropbox connection " + connectionId;
      LOGGER.error(msg);
      throw new ContentHubException(msg);
    }

    try {
      DbxRequestConfig config = new DbxRequestConfig(displayName);

      String proxyHost = settings.getProxyHost();
      Integer proxyPort = settings.getProxyPort();
      String proxyType = settings.getProxyType();

      if (proxyType != null && proxyHost != null && proxyPort != null) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(proxyHost, proxyPort);
        Proxy proxy = new Proxy(Proxy.Type.valueOf(proxyType.toUpperCase()), inetSocketAddress);
        StandardHttpRequestor.Config requestorConfig = StandardHttpRequestor.Config.builder().withProxy(proxy).build();
        StandardHttpRequestor requestor = new StandardHttpRequestor(requestorConfig);

        config = DbxRequestConfig.newBuilder(displayName)
                .withHttpRequestor(requestor)
                .build();
      }

      client = new DbxClientV2(config, accessToken);
    } catch (Exception e) {
      String msg = "Failed to create Dropbox client: " + e.getMessage();
      LOGGER.error(msg, e);
      throw new ContentHubException(msg, e);
    }
  }

  @NonNull
  @Override
  public Folder getRootFolder(@NonNull ContentHubContext context) throws ContentHubException {
    String displayName = settings.getDisplayName();
    ContentHubObjectId rootId = new ContentHubObjectId(connectionId, connectionId);
    return new DropboxFolder(rootId, null, displayName);

  }

  @Nullable
  @Override
  public Item getItem(@NonNull ContentHubContext context, @NonNull ContentHubObjectId id) throws ContentHubException {
    Metadata fileMetadata = getFileMetadata(id);
    if (fileMetadata == null) {
      LOGGER.warn("Dropbox item not found for connector id " + id);
      return null;
    }
    return new DropboxItem(id, fileMetadata, client);
  }

  @Nullable
  @Override
  public Folder getFolder(@NonNull ContentHubContext context, @NonNull ContentHubObjectId id) throws ContentHubException {
    Metadata fileMetadata = getFileMetadata(id);
    String displayName = fileMetadata.getName();
    return new DropboxFolder(id, fileMetadata, displayName);
  }

  @NonNull
  @Override
  public GetChildrenResult getChildren(@NonNull ContentHubContext context, @NonNull Folder folder, @Nullable PaginationRequest paginationRequest) {
    List<ContentHubObject> children = new ArrayList<>();

    try {
      ListFolderBuilder listFolderBuilder = client.files().listFolderBuilder(((DropboxFolder) folder).isRoot() ? "" : ((DropboxFolder) folder).getPathDisplay());
      ListFolderResult result = listFolderBuilder.withRecursive(false).start();

      for (Metadata entry : result.getEntries()) {
        ContentHubObjectId id = new ContentHubObjectId(connectionId, entry.getPathDisplay());
        if (entry instanceof FolderMetadata) {
          children.add(new DropboxFolder(id, entry, entry.getName()));
        } else {
          children.add(new DropboxItem(id, entry, client));
        }
      }

    } catch (DbxException e) {
      LOGGER.error("Failed to read dropbox entries for folder " + folder.getName() + ": " + e.getMessage(), e);
    }

    return new GetChildrenResult(children);
  }

  @Nullable
  @Override
  public Folder getParent(@NonNull ContentHubContext context, @NonNull ContentHubObject contentHubObject) throws ContentHubException {
    if (!contentHubObject.getId().equals(getRootFolder(context).getId())) {
      return getRootFolder(context);
    }
    return null;
  }

  @Override
  @NonNull
  public ContentHubTransformer transformer() {
    return new DropboxContentHubTransformer();
  }


  //------------------------ Helper ------------------------------------------------------------------------------------

  private Metadata getFileMetadata(ContentHubObjectId id) {
    String path = id.getExternalId();
    try {
      return client.files().getMetadata(path);
    } catch (DbxException e) {
      LOGGER.error("Failed to retrieve dropbox item using path '" + path + ": " + e.getMessage());
      throw new ContentHubException("Failed to retrieve dropbox item using path '" + path + ": " + e.getMessage(), e);
    }
  }
}
