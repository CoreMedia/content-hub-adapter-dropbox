package com.coremedia.labs.plugins.adapters.dropbox;


import com.coremedia.common.util.WordAbbreviator;
import com.coremedia.contenthub.api.BaseFileSystemItem;
import com.coremedia.contenthub.api.ContentHubBlob;
import com.coremedia.contenthub.api.ContentHubDefaultBlob;
import com.coremedia.contenthub.api.ContentHubMimeTypeService;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.ContentHubType;
import com.coremedia.contenthub.api.Item;
import com.coremedia.contenthub.api.exception.ContentHubException;
import com.coremedia.contenthub.api.preview.DetailsElement;
import com.coremedia.contenthub.api.preview.DetailsSection;
import com.coremedia.mimetype.TikaMimeTypeService;
import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.Dimensions;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.GetThumbnailBuilder;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.ThumbnailFormat;
import com.dropbox.core.v2.files.ThumbnailMode;
import com.dropbox.core.v2.files.ThumbnailSize;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import javax.activation.MimeType;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


class DropboxItem extends BaseFileSystemItem implements Item {

  private static final Logger LOG = LoggerFactory.getLogger(DropboxItem.class);

  private static final WordAbbreviator ABBREVIATOR = new WordAbbreviator();
  private final FileMetadata fileMetadata;
  private final TikaMimeTypeService tikaservice;
  private ContentHubMimeTypeService mimeTypeService;
  private DbxClientV2 client;

  public static final String CLASSIFIER_PREVIEW = "preview";

  DropboxItem(ContentHubObjectId id,
              Metadata metadata,
              DbxClientV2 client,
              ContentHubMimeTypeService mimeTypeService,
              Map<ContentHubType, String> itemTypeToContentTypeMapping) {
    super(id, metadata != null ? metadata.getName() : "root", mimeTypeService, itemTypeToContentTypeMapping);
    setClient(client);
    this.fileMetadata = (FileMetadata) getFileMetadata(id);
    this.tikaservice = new TikaMimeTypeService();
    tikaservice.init();
    this.mimeTypeService = mimeTypeService;
  }

  FileMetadata getFileMetadata() {
    return fileMetadata;
  }

  @NonNull
  @Override
  public String getName() {
    return fileMetadata.getName();
  }

  @Nullable
  @Override
  public String getDescription() {
    if (fileMetadata.getExportInfo() != null) {
      return HtmlUtils.htmlUnescape(fileMetadata.getPathDisplay());
    }
    return null;
  }

  @NonNull
  @Override
  public String getCoreMediaContentType() {
    if (tikaservice != null) {
      String type = tikaservice.getMimeTypeForResourceName(getName());

      if (type.contains("image")) {
        return "CMPicture";
      } else if (type.contains("audio")) {
        return "CMAudio";
      } else if (type.contains("video")) {
        return "CMVideo";
      } else if (type.equals("application/pdf")) {
        return "CMDownload";
      } else {
        return "CMArticle";
      }
    } else {
      return "CMDownload";
    }
  }

  @NonNull
  @Override
  public List<DetailsSection> getDetails() {
    ContentHubBlob blob = getBlob(CLASSIFIER_PREVIEW);
    return List.of(
            new DetailsSection("main", List.of(
                    new DetailsElement<>(getName(), false, Objects.requireNonNullElse(blob, SHOW_TYPE_ICON))
            ), false, false, false),
            new DetailsSection("metadata", List.of(
                    new DetailsElement<>("text", formatPreviewString(getDescription())),
                    new DetailsElement<>("author", formatPreviewString(fileMetadata.getSharingInfo() != null ? fileMetadata.getSharingInfo().getModifiedBy() : null)),
                    new DetailsElement<>("published", formatPreviewDate(fileMetadata.getServerModified())),
                    new DetailsElement<>("lastModified", formatPreviewDate(fileMetadata.getClientModified())),
                    new DetailsElement<>("link", formatPreviewString(fileMetadata.getSymlinkInfo() != null ? fileMetadata.getSymlinkInfo().getTarget() : null)),
                    new DetailsElement<>("dimensions", formatPreviewString(fileMetadata.getMediaInfo() != null ? formatDimension(fileMetadata.getMediaInfo().getMetadataValue().getDimensions()) : null))
            ).stream().filter(p -> Objects.nonNull(p.getValue())).collect(Collectors.toUnmodifiableList())));
  }

  @Nullable
  @Override
  public ContentHubBlob getBlob(String classifier) {
    try {
      MimeType contentType = mimeTypeService.mimeTypeForResourceName(getName());
      long size = fileMetadata.getSize();
      DbxDownloader<FileMetadata> downloader;
      if (CLASSIFIER_PREVIEW.equals(classifier) || ContentHubBlob.THUMBNAIL_BLOB_CLASSIFIER.equals(classifier)) {
        String id = fileMetadata.getId();
        GetThumbnailBuilder thumbnailBuilder = getClient().files().getThumbnailBuilder(id)
                .withSize(ContentHubBlob.THUMBNAIL_BLOB_CLASSIFIER.equals(classifier) ? ThumbnailSize.W64H64 : ThumbnailSize.W480H320)
                .withFormat(ThumbnailFormat.JPEG)
                .withMode(ThumbnailMode.BESTFIT);
        downloader = thumbnailBuilder.start();
        contentType = new MimeType("image/jpeg");
        size = -1;
      } else {
        downloader = getClient().files().download(fileMetadata.getPathDisplay());
      }
      ContentHubBlob blob = new ContentHubDefaultBlob(
              this,
              classifier,
              contentType,
              size,
              downloader::getInputStream,
              null);
      return blob;
    } catch (Exception e) {
      LOG.error("Cannot create blob for {}. {}", fileMetadata, e);
    }
    return null;
  }

  @Nullable
  @Override
  public ContentHubBlob getThumbnailBlob() {
    return getBlob(ContentHubBlob.THUMBNAIL_BLOB_CLASSIFIER);
  }

  @Nullable
  private String formatPreviewString(@Nullable String str) {
    return str == null ? null : ABBREVIATOR.abbreviateString(str, 240);
  }

  @Nullable
  private Calendar formatPreviewDate(@Nullable Date date) {
    if (date == null) {
      return null;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar;
  }

  public DbxClientV2 getClient() {
    return client;
  }

  public void setClient(DbxClientV2 client) {
    this.client = client;
  }

  //-------------------# Helper #-------------------//

  private Metadata getFileMetadata(ContentHubObjectId id) {
    String path = id.getExternalId();
    try {
      return getClient().files().getMetadataBuilder(path).withIncludeMediaInfo(true).start();
    } catch (DbxException e) {
      throw new ContentHubException("Failed to retrieve dropbox item using path '" + path + ": " + e.getMessage(), e);
    }
  }

  private String formatDimension(Dimensions dimensions) {
    return String.format("%dx%dpx", dimensions.getWidth(), dimensions.getHeight());
  }
}
