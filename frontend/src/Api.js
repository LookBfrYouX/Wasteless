/*
 * Created on Wed Feb 10 2021
 *
 * The Unlicense
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or distribute
 * this software, either in source code form or as a compiled binary, for any
 * purpose, commercial or non-commercial, and by any means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors of this
 * software dedicate any and all copyright interest in the software to the public
 * domain. We make this dedication for the benefit of the public at large and to
 * the detriment of our heirs and successors. We intend this dedication to be an
 * overt act of relinquishment in perpetuity of all present and future rights to
 * this software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

/**
 * Declare all available services here
 */

import axios from 'axios';
import {ApiRequestError} from "./ApiRequestError";
import {constants} from "./constants";

const SERVER_URL = process.env.VUE_APP_SERVER_ADD;

const instance = axios.create({
  baseURL: SERVER_URL,
  timeout: constants.API.TIMEOUT_SHORT,
  withCredentials: true
});

const instanceLongTimeouts = axios.create({
  baseURL: SERVER_URL,
  timeout: constants.API.TIMEOUT_LONG,
  withCredentials: true
});

export const Api = {
  /**
   * Sends login request
   * @param props object with 'email' and 'password'
   * @return promise. If it fails, the error will have the `userFacingErrorMessage` property
   */
  login: (props) => {
    return instance.post("/login", props).catch(error => {
      throw ApiRequestError.createFromMessageMap(error, {
        400: "Your email or password is incorrect"
      });
    })
  },

  /**
   *
   * @param {object} props with properties:
   * `userId`
   * @returns promise. If it fails the error will be shown using the `userFacingErrorMessage` property
   */
  makeAdmin: async (userId) => {
    return instance.put(`/users/${userId}/makeAdmin`).catch(error => {
      throw ApiRequestError.createFromMessageMap(error, {
        403: "You must be an administrator to give others admin permissions",
        406: "Invalid user ID given"
      });
    })
  },

  /**
   *
   * @param {object} props with properties:
   * `userId`
   * @returns promise. If it fails the error will be shown to user using the `userFacingErrorMessage` property
   */
  revokeAdmin: async (userId) => {
    return instance.put(`/users/${userId}/revokeAdmin`).catch(error => {
      throw ApiRequestError.createFromMessageMap(error, {
        403: "You must be an administrator to revoke admin permissions",
        406: "Invalid user ID given",
        409: "Cannot revoke the DGAA's administrator privileges"
      });
    })
  },

  /**
   *
   * @param {object} props with properties:
   *   firstName`, `middleName`, `lastName`, `nickname`,
   *  `email`, `password`, `dateOfBirth`, `homeAddress`, `phoneNumber`, `bio`
   * @return promise. If it fails, the error will have the `userFacingErrorMessage` property
   */
  signUp: (props) => {
    return instance.post("/users", props).catch(error => {
      throw ApiRequestError.createFromMessageMap(error, {
        409: "Your email address has already been registered"
      });
    });
  },

  /**
   *
   * @param {*} id ID of user to fetch
   * @return promise. If it fails, the error will have the `userFacingErrorMessage` property
   */
  profile: (id) => {
    return instance.get(`/users/${id}`).catch(error => {
      throw ApiRequestError.createFromMessageMap(error, {
        406: "The user does not exist"
      });
    });
  },

  /**
   *
   * @param {*} id ID of business to fetch
   * @return promise. If it fails, the error will have the `userFacingErrorMessage` property
   */
  businessProfile: (id) => {
    return instance.get(`/businesses/${id}`).catch(error => {
      throw ApiRequestError.createFromMessageMap(error, {
        406: "Information for this business was not found"
      });
    });
  },

  /**
   *
   * @param {object} props with properties:
   * `name`, `description`, `homeAddress`, `businessType`
   * @return promise. If it fails, the error will have the `userFacingErrorMessage` property
   */
  registerBusiness: (props) => {
    return instance.post("/businesses", props).catch(error => {
      throw ApiRequestError.createFromMessageMap(error, {});
    });
  },

  /**
   *
   * @param {*} id ID of business
   * @param {object} props with properties:
   * `name`, `id`, `description`, `recommendedRetailPrice`
   * @return promise. If it fails, the error will have the `userFacingErrorMessage` property
   */
  createProduct: (id, props) => {
    return instance.post(`/businesses/${id}/products`, props).catch(error => {
      throw ApiRequestError.createFromMessageMap(error, {
        403: "You don't have permission to add products to this business"
      });
    });
  },

  /**
   * Gets all products given in a business catalog
   * @param id ID of business
   * @returns promise. If it fails, the error will have the `userFacingErrorMessage` property
   */
  getProducts: (id) => {
    return instance.get(`/businesses/${id}/products`).catch(error => {
      throw ApiRequestError.createFromMessageMap(error);
    });
  },

  /**
   * Sends a search query
   * @param searchQuery
   * @returns promise. If it fails, the error will have the `userFacingErrorMessage` property
   */
  search: (searchQuery) => {
    return instance.get(
        `/users/search?searchQuery=${encodeURIComponent(searchQuery)}`)
    .catch(error => {
      throw ApiRequestError.createFromMessageMap(error);
    });
  },
  /**
   * Invalidates user cookie on back end, hence logging user out
   * @returns {Promise<AxiosResponse<any> | void>} If it fails, the error will have the `userFacingErrorMessage` property
   */
  logOut: () => {
    return instance.get("/logout").catch(error => {
      throw ApiRequestError.createFromMessageMap(error);
    });
  },

  /**
   * Calls Photon API to fetch address suggestions
   * @param {*} query query string
   * @returns {Promise<Object[]>} array of photon response objects, or error
   */
  addressSuggestions: async function (query) {
    return fetch(`https://photon.komoot.io/api?q=${encodeURIComponent(query)}`)
    .then(res => res.json())
    .catch(err => {
      throw ApiRequestError.createFromMessageMap(err)
    });
  },

  /**
   * Logs the user out client-side and redirects to a logout page
   * Call using `Api.handle401.call(this, err)` from the vue component
   * If `this.$stateStore` and `this.$router`  are not defined, likely because
   * `.call` has not been used, or because the jest test has not mocked these, an error message will
   * be printed and the method will return false.
   * @param {ApiRequestError} error handle logout when a 401 is returned by the api
   * @param {this} callee
   * @return {Boolean} true if it was a 401 error
   */
  handle401: async function (err) {
    if (this.$stateStore === undefined || this.$router === undefined) {
      console.warn(
          "[Api.js, handle401]. Call this method using `.call(this, err)` - need access to Vue's state and router variables, which this does not have access to");
      return false;
    }
    if (err && err.status === 401) {
      await this.$stateStore.actions.deleteAuthUser();
      await this.$router.push({name: "error401"});
      return true;
    }

    return false;
  },

  /**
   * Uploads an product image and sends post request to ImageController
   * @param image is a file object with a name property that contains original file name
   * @param businessId is an id of business
   * @param productId is an id of product
   * @returns {Promise<AxiosResponse<any>>}
   */
  uploadProductImage: (image, businessId, productId) => {
    const formData = new FormData();
    formData.append("image", image);
    return instanceLongTimeouts.post(
        `/businesses/${businessId}/products/${productId}/images`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }).catch(error => {
      throw ApiRequestError.createFromMessageMap(error, {
        403: "You don't have permission to upload the images.",
        406: "Could not upload the image.",
        413: "The image you uploaded was too large. Please upload a smaller image"
      });
    });
  },

  /**
   * Deletes product image and sends delete request to ImageController
   * @param businessId is an id of business
   * @param productId is an id of product
   * @param imageId is an id of image
   * @returns {Promise<AxiosResponse<any>>}
   */
  deleteProductImage: (businessId, productId, imageId) => {
    return instance.delete(
        `/businesses/${businessId}/products/${productId}/images/${imageId}`)
    .catch(error => {
      throw ApiRequestError.createFromMessageMap(error, {
        403: "You don't have permission to delete the image.",
        406: "Could not delete the image."
      });
    });
  },

  /**
   * Changes primary image of product images of given business and product
   * @param businessId is an id of business
   * @param productId is an id of product
   * @param imageId is an id of image
   * @returns {Promise<AxiosResponse<any>>}
   */
  changePrimaryImage: (businessId, productId, imageId) => {
    return instance.put(
        `/businesses/${businessId}/products/${productId}/images/${imageId}/makeprimary`)
    .catch(error => {
      throw ApiRequestError.createFromMessageMap(error, {
        403: "You don't have permission to set primary image.",
        406: "Could not set primary image."
      });
    });
  },

  /**
   * Adds product and extra info to businesses inventory
   *
   * @param businessId of the business the item will be added to
   * @param item to be added to the businesses inventory
   * @returns {Promise<AxiosResponse<any> | void>}
   */
  addItemToInventory: (businessId, item) => {
    return instance.post(
        `/businesses/${businessId}/inventory/`, item)
    .catch(error => {
      throw ApiRequestError.createFromMessageMap(error, {
        400: "Bad request: Invalid data supplied",
        403: "Forbidden: Insufficient privileges"
      });
    });
  },

  getBusinessListings: businessId => {
    return instance.get(`/businesses/${businessId}/listings`).catch(err => {
      throw ApiRequestError.createFromMessageMap(err, {
        406: "The business does not exist - either the URL was typed in wrong or the business was deleted"
      });
    })
  },

  /**
   * Get the businesses Inventory from API
   * @param businessId The id of the business
   * @returns {Promise<AxiosResponse<any>>} Promise containing the inventory
   */
  getBusinessInventory: businessId => {
    return instance.get(`/businesses/${businessId}/inventory`).catch(err => {
      throw ApiRequestError.createFromMessageMap(err, {
        403: "You don't have permission view this businesses inventory",
        406: "The business does not exist - either the URL was typed in wrong or the business was deleted"
      });
    })
  },
  addBusinessListings: (businessId, listings) => {
    return instance.post(
        `/businesses/${businessId}/listings`, listings)
    .catch(error => {
      throw ApiRequestError.createFromMessageMap(error, {
        400: err => `An error creating the listing. ${err.response.statusText}`,
        403: "You don't have permission to add the listings"
      });
    });
  }
}