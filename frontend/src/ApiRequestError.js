/**
 * Wrapper around error returned by Axios
 * `userFacingErrorMessage` is always set
 * `status` is the HTTP response code; may be undefined
 */
export class ApiRequestError extends Error {
  static NO_RESPONSE_KEY = "NO_RESPONSE";
  static DEFAULT_KEY = "DEFAULT_RESPONSE";

  static DEFAULT_MESSAGES_MAP = {
      401: "You must be logged in to access this page",
      403: "You don't have permission to access this page",
      [this.NO_RESPONSE_KEY]: "Could not connect to server. Try again in a few minutes",
      [this.DEFAULT_KEY]: err => `An unknown error (${err.response.status}) occurred\n${err}`
  }

  /**
   * Error message to show user
   */
  userFacingErrorMessage = "";
  
  /**
   * HTTP status code. May be undefined
   */
  status = undefined;
  
  constructor(message, error) {
      // Copy all properties of the error
      super(error);
      Object.assign(this, error);
      this.userFacingErrorMessage = message; // but add user facing error message
      if (error !== undefined && error.response !== undefined && typeof error.response.status == "number") {
          this.status = error.response.status;
      }
      this.message = `API Request Error ${status? `(${status})`: ""} - ${this.userFacingErrorMessage}`;
  }

  /**
   * The caller provides a map from HTTP codes to error messages; this function will take the 
   * HTTP status code and pick which error message should be used, setting a default if there is
   * no entry in the object
   * @param {*} error Axios error
   * @param {Map<number|string, string|(Error) => string>} errorMessages 
   * map between HTTP code and either string error message or function taking error as argument
   * Keys are HTTP error codes with the exception of `NO_RESPONSE`/`DEFAULT_RESPONSE`
   */
  static createFromMessageMap(error, errorMessages = undefined) {
      let message = "";
      if (typeof errorMessages !== "object") errorMessages = {};

      /**
       * Gets the string response for the given HTTP error code, using either a default or
       * the one provided in `errorMessages`
       * @param {*} key key of the object; number or `NO_RESPONSE`/`DEFAULT_RESPONSE`
       * @returns string
       */
      const callOrGet = (key) => {
          // get the provided value, or if undefined, the default
          const messageOrFunction = (errorMessages[key] !== undefined)? errorMessages[key]: this.DEFAULT_MESSAGES_MAP[key];
          // Check if it is a function; if so, call the function with the error message
          return typeof messageOrFunction == "string"? messageOrFunction: messageOrFunction(error);
      }

      if (error === undefined || error.response === undefined) {
          message = callOrGet(this.NO_RESPONSE_KEY);
      } else if (typeof error.response.status !== "number") {
          message = callOrGet(this.DEFAULT_KEY);
      } else {
          const code = error.response.status;
          if (errorMessages[code] !== undefined || this.DEFAULT_MESSAGES_MAP[code] !== undefined) message = callOrGet(code);
          else message = callOrGet(this.DEFAULT_KEY); // no default handlers, so use default response
      }

      return new ApiRequestError(message, error);
  }

}
