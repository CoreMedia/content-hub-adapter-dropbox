import ContentHub_properties from "@coremedia/studio-client.main.content-hub-editor-components/ContentHub_properties";
import CopyResourceBundleProperties from "@coremedia/studio-client.main.editor-components/configuration/CopyResourceBundleProperties";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import ContentHubDropbox_properties from "./ContentHubDropbox_properties";

interface ContentHubStudioDropboxPluginConfig extends Config<StudioPlugin> {
}

class ContentHubStudioDropboxPlugin extends StudioPlugin {
  declare Config: ContentHubStudioDropboxPluginConfig;

  static readonly xtype: string = "com.coremedia.blueprint.studio.contenthub.dropbox.ContentHubStudioDropboxPlugin";

  constructor(config: Config<ContentHubStudioDropboxPlugin> = null) {
    super(ConfigUtils.apply(Config(ContentHubStudioDropboxPlugin, {

      configuration: [
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, ContentHub_properties),
          source: resourceManager.getResourceBundle(null, ContentHubDropbox_properties),
        }),
      ],

    }), config));
  }
}

export default ContentHubStudioDropboxPlugin;
