import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import icon from "./icons/dropbox_24.svg";
import SvgIconUtil from "@coremedia/studio-client.base-models/util/SvgIconUtil";

/**
 * Interface values for ResourceBundle "ContentHubDropbox".
 * @see ContentHubDropbox_properties#INSTANCE
 */
interface ContentHubDropbox_properties {

/**
 *Dropbox
 */
  author_header: string;
  lastModified_header: string;
  folder_type_dbx_folder_name: string;
  folder_type_dbx_folder_icon: string;
  adapter_type_dropbox_name: string;
  adapter_type_dropbox_icon: string;
  item_type_dbx_file_name: string;
  item_type_dbx_file_icon: string;
  metadata_sectionName: string;
  text_sectionItemKey: string;
  author_sectionItemKey: string;
  published_sectionItemKey: string;
  lastModified_sectionItemKey: string;
  link_sectionItemKey: string;
  dimensions_sectionItemKey: string;
  "item_type_vnd.openxmlformats-officedocument.wordprocessingml.document_name": string
  "item_type_vnd.openxmlformats-officedocument.wordprocessingml.document_icon": string,
  "item_type_vnd.openxmlformats-officedocument.spreadsheetml.sheet_name": string,
  "item_type_vnd.openxmlformats-officedocument.spreadsheetml.sheet_icon": string,
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "ContentHubDropbox".
 * @see ContentHubDropbox_properties
 */
const ContentHubDropbox_properties: ContentHubDropbox_properties = {
  author_header: "Author",
  lastModified_header: "Last Modified",
  folder_type_dbx_folder_name: "Folder",
  folder_type_dbx_folder_icon: CoreIcons_properties.folder,
  adapter_type_dropbox_name: "Dropbox",
  adapter_type_dropbox_icon: SvgIconUtil.getIconStyleClassForSvgIcon(icon),
  item_type_dbx_file_name: "File",
  item_type_dbx_file_icon: CoreIcons_properties.type_external_content,
  metadata_sectionName: "Metadata",
  text_sectionItemKey: "Text",
  author_sectionItemKey: "Author",
  published_sectionItemKey: "Published",
  lastModified_sectionItemKey: "Last modified",
  link_sectionItemKey: "Link",
  dimensions_sectionItemKey: "Dimensions",
  "item_type_vnd.openxmlformats-officedocument.wordprocessingml.document_name": "Word",
  "item_type_vnd.openxmlformats-officedocument.wordprocessingml.document_icon": CoreIcons_properties.mimetype_doc,
  "item_type_vnd.openxmlformats-officedocument.spreadsheetml.sheet_name": "Excel",
  "item_type_vnd.openxmlformats-officedocument.spreadsheetml.sheet_icon": CoreIcons_properties.mimetype_doc,
};

export default ContentHubDropbox_properties;
