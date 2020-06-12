package com.coremedia.blueprint.contenthub.adapters.dropbox;

import com.coremedia.cap.common.Blob;
import com.coremedia.contenthub.api.*;
import com.coremedia.cotopaxi.common.blobs.BlobServiceImpl;
import com.coremedia.mimetype.TikaMimeTypeService;
import com.coremedia.util.TempFileFactory;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.web.util.HtmlUtils;

import javax.activation.MimeTypeParseException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

class DropboxContentHubTransformer implements ContentHubTransformer {

  private TikaMimeTypeService tikaMimeTypeService;

  @Override
  @NonNull
  public ContentModel transform(Item item, ContentHubAdapter contentHubAdapter, ContentHubContext contentHubContext) {
    if (!(item instanceof DropboxItem)) {
      throw new IllegalArgumentException("Not my item: " + item);
    }
    return transformDropboxItem((DropboxItem) item);
  }

  @Override
  @Nullable
  public ContentModel resolveReference(ContentHubObject owner, ContentModelReference reference, ContentHubAdapter contentHubAdapter, ContentHubContext contentHubContext) {
    String imageUrl = (String) reference.getData();
    String imageName = ContentCreationUtil.extractNameFromUrl(imageUrl);
    if (imageName == null) {
      return null;
    }
    ContentModel referenceModel = ContentModel.createReferenceModel(imageName, reference.getCoreMediaContentType());
    referenceModel.put("data", new UrlBlobBuilder(owner, "dbxPicture").withUrl(imageUrl).withEtag().build());
    referenceModel.put("title", "Image " + imageName);

    return referenceModel;
  }


  // --- internal ---------------------------------------------------

  @NonNull
  private ContentModel transformDropboxItem(DropboxItem item) {
    ContentModel contentModel = ContentModel.createContentModel(item.getFileMetadata().getName(), item.getId(), item.getCoreMediaContentType());
    contentModel.put("title", item.getName());
    String type = item.getCoreMediaContentType();

    Map<String, Object> additionalProps = new HashMap<>();

    if (type.equals("CMAudio") || type.equals("CMPicture") || type.equals("CMVideo") || type.equals("CMDownload")) {
      additionalProps.putAll(Objects.requireNonNull(getMediaProperties(item.getFileMetadata(), item.getClient())));
    }

    String description = extractDescription(item);
    if (description != null) {
      contentModel.put("detailText", ContentCreationUtil.convertStringToRichtext(description));
    }

    //add additional properties
    additionalProps.forEach(contentModel::put);

    return contentModel;
  }

  @Nullable
  private String extractDescription(@Nullable DropboxItem item) {
    FileMetadata metadata = item == null ? null : item.getFileMetadata();
    String description = metadata == null ? null : HtmlUtils.htmlUnescape(metadata.getPathDisplay());
    return description;
  }

  @Nullable
  private Map<String, Object> getMediaProperties(FileMetadata metadata, DbxClientV2 clientV2) {

    Map<String, Object> result = new HashMap<>();

    try {
      InputStream stream = clientV2.files().downloadBuilder(metadata.getPathDisplay()).start().getInputStream();
      BlobServiceImpl blobService = new BlobServiceImpl(new TempFileFactory(), getTika());
      Blob blob = blobService.fromInputStream(stream, getTika().getMimeTypeForResourceName(metadata.getName()));
      result.put("data", blob);
      stream.close();
      return result;
    } catch (DbxException | IOException | MimeTypeParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  private TikaMimeTypeService getTika() {
    if (tikaMimeTypeService == null) {
      this.tikaMimeTypeService = new TikaMimeTypeService();
      tikaMimeTypeService.init();
    }
    return tikaMimeTypeService;
  }
}