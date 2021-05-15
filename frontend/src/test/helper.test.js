import { ApiRequestError } from "../ApiRequestError";
import { Api } from "./../Api";
jest.mock("./../Api");

import { helper } from "./../helper";

describe("tryGetBusinessName", () => {
  test("200 response", async () => {
    Api.businessProfile.mockResolvedValue({ data: { name: "Bla" }});
    expect(await helper.tryGetBusinessName(1)).toEqual("Bla");
  });

  test("bad response", async () => {
    Api.businessProfile.mockImplementation(() => Promise.reject(new ApiRequestError("FAIL")));
    expect(await helper.tryGetBusinessName(1)).toEqual(null);
  });
})
