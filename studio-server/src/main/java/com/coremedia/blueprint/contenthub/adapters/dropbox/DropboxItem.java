package com.coremedia.blueprint.contenthub.adapters.dropbox;


import com.coremedia.common.util.WordAbbreviator;
import com.coremedia.contenthub.api.*;
import com.coremedia.contenthub.api.exception.ContentHubException;
import com.coremedia.contenthub.api.preview.DetailsElement;
import com.coremedia.contenthub.api.preview.DetailsSection;
import com.coremedia.mimetype.TikaMimeTypeService;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.Dimensions;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.Metadata;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.web.util.HtmlUtils;

import java.util.*;
import java.util.stream.Collectors;

class DropboxItem extends DropboxHubObject implements Item {
  private static final WordAbbreviator ABBREVIATOR = new WordAbbreviator();
  private static final int BLOB_SIZE_LIMIT = 10000000;
  private FileMetadata fileMetadata;
  private TikaMimeTypeService tikaservice;

  DropboxItem(ContentHubObjectId id, Metadata metadata, DbxClientV2 client) {
    super(id, metadata);
    setClient(client);
    this.fileMetadata = (FileMetadata) getFileMetadata(id);
    this.tikaservice = new TikaMimeTypeService();
    tikaservice.init();
  }

  FileMetadata getFileMetadata() {
    return fileMetadata;
  }

  @NonNull
  @Override
  public ContentHubType getContentHubType() {
    return new ContentHubType("dbx_file");
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

      if (type.contains("image"))
        return "CMPicture";
      else if (type.contains("audio"))
        return "CMAudio";
      else if (type.contains("video"))
        return "CMVideo";
      else if (type.equals("application/pdf"))
        return "CMDownload";
      else
        return "CMArticle";
    }
    else
      return "CMArticle";
  }

  @NonNull
  @Override
  public List<DetailsSection> getDetails() {
    return List.of(
            new DetailsSection("main", List.of(
                    new DetailsElement<>(fileMetadata.getName(), false, SHOW_TYPE_ICON)
            ), false, false, false),
            new DetailsSection("metadata", List.of(
                    new DetailsElement<>("text", formatPreviewString(getDescription())),
                    new DetailsElement<>("author", formatPreviewString(fileMetadata.getSharingInfo() != null ? fileMetadata.getSharingInfo().getModifiedBy() : null)),
                    new DetailsElement<>("published", formatPreviewDate(fileMetadata.getServerModified())),
                    new DetailsElement<>("lastModified", formatPreviewDate(fileMetadata.getClientModified())),
                    new DetailsElement<>("link", formatPreviewString(fileMetadata.getSymlinkInfo() != null ? fileMetadata.getSymlinkInfo().getTarget() : null)),
                    new DetailsElement<>("dimension", formatPreviewString(fileMetadata.getMediaInfo() != null ? formatDimension(fileMetadata.getMediaInfo().getMetadataValue().getDimensions()) : null))
            ).stream().filter(p -> Objects.nonNull(p.getValue())).collect(Collectors.toUnmodifiableList())));
  }


  @Nullable
  @Override
  public ContentHubBlob getBlob(String classifier) {
    try {
      return new UrlBlobBuilder(this, classifier).withUrl(getClient().sharing().getFileMetadata(fileMetadata.getPathDisplay()).getPreviewUrl()).withEtag().build();
    } catch (DbxException e) {
      e.printStackTrace();
    }

    return null;
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
    return String.format("Height: %s x Width: %s", dimensions.getHeight(), dimensions.getWidth());
  }
}
