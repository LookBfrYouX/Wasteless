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

const SERVER_URL = process.env.VUE_APP_SERVER_ADD;
const TIMEOUT = 1000;

const instance = axios.create({
    baseURL: SERVER_URL,
    timeout: TIMEOUT,
    withCredentials: true
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
        return instance.post("/login", props).catch(error => {
            let userFacingErrorMessage = NO_SERVER_RESPONSE_ERROR_MESSAGE;
            if (error != undefined && error.response !== undefined) {
                if (error.response.status === 400) {
                    userFacingErrorMessage = "Your email or password is incorrect";
                } else {
                    userFacingErrorMessage = unknownErrorMessage(error);
                }
            }

            error.userFacingErrorMessage = userFacingErrorMessage;
            throw error;
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
            let userFacingErrorMessage = NO_SERVER_RESPONSE_ERROR_MESSAGE
            if (error != undefined && error.response !== undefined) {
                if (error.response.status == 401) {
                    userFacingErrorMessage = "Unauthorized"
                } else if (error.response.status === 406) {
                    userFacingErrorMessage  = "Invalid ID format";
                } else {
                    userFacingErrorMessage = unknownErrorMessage(error);
                }
            }
            error.userFacingErrorMessage = userFacingErrorMessage;
            throw error;
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
            let userFacingErrorMessage = NO_SERVER_RESPONSE_ERROR_MESSAGE
            if (error != undefined && error.response !== undefined) {
                if (error.response.status == 401) {
                    userFacingErrorMessage = "Unauthorized"
                } else if (error.response.status === 406) {
                    userFacingErrorMessage  = "Invalid ID format";
                } else if (error.response.status === 409) {
                    userFacingErrorMessage  = "Cannot Revoke the DGAA's Administrator Privilages";
                } else {
                    userFacingErrorMessage = unknownErrorMessage(error);
                }
            }
            error.userFacingErrorMessage = userFacingErrorMessage;
            throw error;
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
            let userFacingErrorMessage = NO_SERVER_RESPONSE_ERROR_MESSAGE;

            if (error != undefined && error.response != undefined) {
                if (error.response.status == 409) {
                    userFacingErrorMessage = "Your email address has already been registered";
                } else {
                    userFacingErrorMessage = unknownErrorMessage(error);
                }
            }

            error.userFacingErrorMessage = userFacingErrorMessage;
            throw error;
        });
    },

    /**
     *
     * @param {*} id ID of user to fetch
     * @return promise. If it fails, the error will have the `userFacingErrorMessage` property
     */
    profile: (id) => {
        return instance.get(`/users/${id}`).catch(error => {
            let userFacingErrorMessage = NO_SERVER_RESPONSE_ERROR_MESSAGE;

            if (error != undefined && error.response != undefined) {
                if (error.response.status == 401) {
                    userFacingErrorMessage = "You don't have permission to access this page";
                } else if (error.response.status == 405) {
                    userFacingErrorMessage = "Information for the user was not found";
                } else {
                    userFacingErrorMessage = unknownErrorMessage(error);
                }
            }

            error.userFacingErrorMessage = userFacingErrorMessage;
            throw error;
        });
    },

    /**
     *
     * @param {*} id ID of business to fetch
     * @return promise. If it fails, the error will have the `userFacingErrorMessage` property
     */
    businessProfile: (id) => {
        return instance.get(`/businesses/${id}`).catch(error => {
            let userFacingErrorMessage = NO_SERVER_RESPONSE_ERROR_MESSAGE;

            if (error != undefined && error.response != undefined) {
                if (error.response.status == 401) {
                    userFacingErrorMessage = "You don't have permission to access this page";
                } else if (error.response.status == 406) {
                    userFacingErrorMessage = "Information for the user was not found";
                } else {
                    userFacingErrorMessage = unknownErrorMessage(error);
                }
            }

            error.userFacingErrorMessage = userFacingErrorMessage;
            throw error;
        });
    },


    /**
     * Sends a search query
     * @param searchQuery
     * @returns promise. If it fails, the error will have the `userFacingErrorMessage` property
     */
    search: (searchQuery) => {
        return instance.get(`/users/search?searchQuery=${encodeURIComponent(searchQuery)}`)
        .catch(error => {
            let userFacingErrorMessage = NO_SERVER_RESPONSE_ERROR_MESSAGE;

            if (error != undefined && error.response != undefined) {
                if (error.response.status == 401) {
                    userFacingErrorMessage = "You don't have permission to access this page";
                } else {
                    userFacingErrorMessage = unknownErrorMessage(error);
                }
            }
            error.userFacingErrorMessage = userFacingErrorMessage;
            throw error;
        });
    },
    /**
     * Invalidates user cookie on back end, hence logging user out
     * @returns {Promise<AxiosResponse<any> | void>} If it fails, the error will have the `userFacingErrorMessage` property
     */
    logOut: () => {
        // Remove current user from localstorage
        window.localStorage.removeItem("authUser");
        return instance.get("/logout").catch(error => {
            let userFacingErrorMessage = NO_SERVER_RESPONSE_ERROR_MESSAGE;

            if (error != undefined && error.response != undefined) {
                userFacingErrorMessage = unknownErrorMessage(error);
            }
            error.userFacingErrorMessage = userFacingErrorMessage;
            throw error;
        });
    }
}
