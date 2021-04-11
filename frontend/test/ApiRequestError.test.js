import { ApiRequestError }  from "./../src/ApiRequestError";

const customMessages = {
  [ApiRequestError.NO_RESPONSE_KEY]: "A",
  [ApiRequestError.DEFAULT_KEY]: "B",
  401: "401",
  200: "200"
};

const getError = code => ({response: {status: code}});

describe("createFromMessageMap", () => {
  test("no response default handler", () => {
    expect(ApiRequestError.createFromMessageMap(undefined, {}).userFacingErrorMessage)
    .toEqual(ApiRequestError.DEFAULT_MESSAGES_MAP[ApiRequestError.NO_RESPONSE_KEY]);
  });
  test("no response given handler", () => {
    expect(ApiRequestError.createFromMessageMap(undefined, customMessages).userFacingErrorMessage)
    .toEqual("A");
  });

  test("unknown response default handler", () => {
    expect(ApiRequestError.createFromMessageMap(getError(1000), {}).userFacingErrorMessage)
    .toEqual(ApiRequestError.DEFAULT_MESSAGES_MAP[ApiRequestError.DEFAULT_KEY](getError(1000)));
  });

  test("unknown response given default handler", () => {
    expect(
        ApiRequestError.createFromMessageMap(getError(1000), {
        [ApiRequestError.DEFAULT_KEY]: "Z"
        }).userFacingErrorMessage
    ).toEqual("Z");
  });
  test("unknown response given handler", () => {
    expect(ApiRequestError.createFromMessageMap(getError(200), customMessages).userFacingErrorMessage)
    .toEqual("200");
  });

  test("known response default handler", () => {
    expect(ApiRequestError.createFromMessageMap(getError(401), {}).userFacingErrorMessage)
    .toEqual(ApiRequestError.DEFAULT_MESSAGES_MAP[401]);
  });

  test("known response given handler", () => {
    expect(ApiRequestError.createFromMessageMap(getError(401), customMessages).userFacingErrorMessage)
    .toEqual("401");
  });
});
