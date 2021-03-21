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

import axios from 'axios'

const SERVER_URL = "http://localhost:3000/api/"
const TIMEOUT = 1000;

const instance = axios.create({
  baseURL: SERVER_URL,
  timeout: TIMEOUT
});

const NO_SERVER_RESPONSE_ERROR_MESSAGE = "Could not connect to server. Try again in a few minutes";

let unknownErrorMessage = err => `An unknown error (${err.response.status}) occurred: ${err}`;

export default {
  /**
   * Sends login request
   * @param props object with 'email' and 'password'
   * @return promise. If it fails, the error will have the `userFacingErrorMessage` property
   */
  login: (props) => {
    return instance.post("login", props).catch(err => {
      let userFacingErrorMessage = NO_SERVER_RESPONSE_ERROR_MESSAGE;

      if (err.response != undefined) {
        if (err.response.status == 400) {
          userFacingErrorMessage = "Your email or password is incorrect";
        } else {
          userFacingErrorMessage = unknownErrorMessage(err);
        }
      }

      err.userFacingErrorMessage = userFacingErrorMessage;
      throw err;
    });
  },


  /**
   * 
   * @param {object} props with properties:
   *   firstName`, `middleName`, `lastName`, `nickname`,
   *  `email`, `password`, `dateOfBirth`, `homeAddress`, `phoneNumber`, `bio`
   * @return promise. If it fails, the error will have the `userFacingErrorMessage` property
   */
  signUp: (props) => {
    return instance.post("users", props).catch(err => {
      let userFacingErrorMessage = NO_SERVER_RESPONSE_ERROR_MESSAGE;

      if (err.response != undefined) {
        if (err.response.status == 409) {
          userFacingErrorMessage = "Your email address has already been registered";
        } else {
          userFacingErrorMessage = unknownErrorMessage(err);
        }
      }

      err.userFacingErrorMessage = userFacingErrorMessage;
      throw err;
    });
  },

  /**
   * 
   * @param {*} id ID of user to fetch
   * @return promise. If it fails, the error will have the `userFacingErrorMessage` property
   */
  profile: (id) => {
    return instance.get(`/users/${id}`).catch(err => {
      let userFacingErrorMessage = NO_SERVER_RESPONSE_ERROR_MESSAGE;

      if (err.response != undefined) {
        if (err.response.status == 401) {
          userFacingErrorMessage = "You don't have permission to access this page";
        } else if (err.response.status == 405) {
          userFacingErrorMessage = "Information for the user was not found";
        } else {
          userFacingErrorMessage = unknownErrorMessage(err);
        }
      }

      err.userFacingErrorMessage = userFacingErrorMessage;
      throw err;
    });
  },
  search: (searchQuery) => {
    return instance.get(`/users/search?searchQuery=${encodeURIComponent(searchQuery)}`).catch(err => {
      let userFacingErrorMessage = NO_SERVER_RESPONSE_ERROR_MESSAGE;

      if (err.response != undefined) {
        if (err.response.status == 401) {
          userFacingErrorMessage = "You don't have permission to access this page";
        } else {
          userFacingErrorMessage = unknownErrorMessage(err);
        }
      }
      err.userFacingErrorMessage = userFacingErrorMessage;
      throw err;
    });
  }
}