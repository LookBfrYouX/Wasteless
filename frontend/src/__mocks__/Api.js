/**
 * If there's an API call before mount or something and so you can't use `wrapper.vm.methodName` to override the method
 * (overriding of method occurs after the before mount hook), use this to mock the Api.js methods.
 * Use `jest.mock("./path/to/the/real/Api.js")` to mock, then use `const { Api } = require("./path/to/the/real/Api.js")`
 * and `Api._setMethod()` to set the response for a given method.
 *
 * e.g. `Api.setMethod("signUp", () => return Promise.resolve({data: {userId: 10}})`
 * 
 * See AddressForm.jest.js, commit `90b637ffd357f9ead0a84ff4fe0b26c47938d84f` for example of use
 */

let _Api = {
  /**
   * Sets Api method
   * @param name name of Api method
   * @param method Api method
   * @private
   */
  _setMethod: (name, method) => _Api[name] = method
};

_Api["handle401"] = () => {};
export let Api = _Api;
