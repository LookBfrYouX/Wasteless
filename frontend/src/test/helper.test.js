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
});

describe("goToProfile", () => {
  const $router = () => ({
    push: jest.fn(),
    go: jest.fn()
  });

  const user$route = (id = undefined, keepParams = false) => {
    let path = {
      name: "profile",
      params: {
        userId: id
      }
    };

    if (id == undefined && !keepParams) delete path.params;
    return path;
  }

  const business$route = (id = 1) => ({
    name: "businessProfile",
    params: {
      businessId: id
    }
  });

  const unrelated$route = () => ({
    name: "HiMax",
    params: {
      userId: 314,
      businessId: 592
    }
  })
  
  const business = {
    id: 1
  };

  test("on random page, acting as business", async () => {
    const router = $router();
    await helper.goToProfile(business, router, unrelated$route());
    expect(router.push).toHaveBeenCalledWith(business$route());
  });

  test("on random page, acting as user", async () => {
    const router = $router();
    await helper.goToProfile(null, router, unrelated$route());
    expect(router.push).toHaveBeenCalledWith(user$route());
  });


  test("on other user page, acting as user", async () => {
    const router = $router();
    await helper.goToProfile(null, router, user$route(122358));
    expect(router.push).toHaveBeenCalledWith(user$route());
  });

  test("on other business page, acting as business", async () => {
    const router = $router();
    await helper.goToProfile(business, router, business$route(4));
    expect(router.push).toHaveBeenCalledWith(business$route());
  });
  
  test("on own user page, acting as user", async () => {
    const router = $router();
    await helper.goToProfile(null, router, user$route(undefined, true));
    expect(router.go).toHaveBeenCalled();
  });
  
  test("on own business page, acting as business", async () => {
    const router = $router();
    await helper.goToProfile(business, router, business$route());
    expect(router.go).toHaveBeenCalled();
  });

  test("on own user page, acting as business", async () => {
    const router = $router();
    await helper.goToProfile(business, router, user$route());
    expect(router.push).toHaveBeenCalledWith(business$route());
  });
});
