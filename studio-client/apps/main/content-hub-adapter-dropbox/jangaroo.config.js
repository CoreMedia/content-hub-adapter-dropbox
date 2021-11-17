/** @type { import('@jangaroo/core').IJangarooConfig } */
module.exports = {
  type: "code",
  extName: "com.coremedia.labs.plugins__studio-client.content-hub-adapter-dropbox",
  extNamespace: "com.coremedia.labs.plugins.adapters.dropbox.client",
  sencha: {
    studioPlugins: [
      {
        mainClass: "com.coremedia.labs.plugins.adapters.dropbox.client.ContentHubStudioDropboxPlugin",
        name: "Content Hub",
      },
    ],
  },
  command: {
    build: {
      ignoreTypeErrors: true
    },
  },
};
