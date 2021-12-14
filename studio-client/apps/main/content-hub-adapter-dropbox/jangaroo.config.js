/** @type { import('@jangaroo/core').IJangarooConfig } */
module.exports = {
  type: "code",
  sencha: {
    name: "com.coremedia.labs.plugins__studio-client.content-hub-adapter-dropbox",
    namespace: "com.coremedia.labs.plugins.adapters.dropbox.client",
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
