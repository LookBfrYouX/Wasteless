/**
 * Helper methods that may be used by multiple pages. It is added to the vue prototype so
 * the component does not need to explicitly import this class
 */
export const helper = {
  /**
   * Generates path to asset, prepending VUE_APP_BASE_URL
   * @param {string} absolute path to (public) asset
   * @return path to asset
   */
  // asset(path) {
  //   if (path.length && path[0] == "/") path = path.substr(1); // Remove leading / if present
  //   return process.env.VUE_APP_BASE_URL + path;
  // }
}
