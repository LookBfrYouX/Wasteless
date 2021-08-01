import {ApiRequestError} from "@/ApiRequestError";
import {Api} from "@/Api";
import {helper} from "@/helper";

jest.mock("@/Api");

describe("tryGetBusinessName", () => {
  test("200 response", async () => {
    Api.businessProfile.mockResolvedValue({data: {name: "Bla"}});
    expect(await helper.tryGetBusinessName(1)).toEqual("Bla");
  });

  test("bad response", async () => {
    Api.businessProfile.mockImplementation(
        () => Promise.reject(new ApiRequestError("FAIL")));
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
      name: "UserDetail",
      params: {
        userId: id
      }
    };

    if (id == undefined && !keepParams) {
      delete path.params;
    }
    return path;
  }

  const business$route = (id = 1) => ({
    name: "BusinessDetail",
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

describe("months since registration", () => {
  test("0 months", () => {
    expect(helper.generateTimeSinceRegistrationText(
        new Date(2020, 1, 1), new Date(2020, 1, 28)
    )).toEqual("0 months");
  });

  test("1 month", () => {
    expect(helper.generateTimeSinceRegistrationText(
        new Date(2020, 1, 1), new Date(2020, 2, 1)
    )).toEqual("1 month");
  });

  test("5 months", () => {
    expect(helper.generateTimeSinceRegistrationText(
        new Date(2020, 1, 1), new Date(2020, 6, 28)
    )).toEqual("5 months");
  });

  test("1 year", () => {
    expect(helper.generateTimeSinceRegistrationText(
        new Date(2020, 1, 1), new Date(2021, 1, 28)
    )).toEqual("1 year, 0 months");
  });

  test("13 months", () => {
    expect(helper.generateTimeSinceRegistrationText(
        new Date(2020, 1, 1), new Date(2021, 2, 28)
    )).toEqual("1 year, 1 month");
  });

  test("2 years and a few months", () => {
    expect(helper.generateTimeSinceRegistrationText(
        new Date(2020, 1, 1), new Date(2022, 8, 28)
    )).toEqual("2 years, 7 months");
  });
});

describe("Date string format", () => {
  test("standard jan", () => {
    expect(helper.formatDate(new Date(2000, 0, 1))).toEqual(
        "1 January, 2000");
  });

  test("standard dec", () => {
    expect(helper.formatDate(new Date(2000, 11, 31))).toEqual(
        "31 December, 2000");
  });

  test("ISO string", () => {
    expect(helper.formatDate("2021-03-02T05:35:03")).toEqual(
        "2 March, 2021");
  });

  test("ISO string no time", () => {
    expect(helper.formatDate("1949-05-09")).toEqual("9 May, 1949");
  });
});

describe("addressToString", () => {
  const address = {
    streetNumber: "1",
    streetName: "STREET",
    suburb: "S",
    postcode: "P",
    city: "CITY",
    region: "R",
    country: "C"
  };

  test("full address", () => {
    expect(helper.addressToString(address)).toEqual(
        "1 STREET, S, CITY, R, P, C");
  });

  test("full address, no street number", () => {
    let addr = Object.assign({}, address);
    delete addr.streetNumber;
    expect(helper.addressToString(addr)).toEqual("STREET, S, CITY, R, P, C");
  });

  test("full address, blank street number", () => {
    let addr = Object.assign({}, address);
    addr.streetNumber = "";
    expect(helper.addressToString(addr)).toEqual("STREET, S, CITY, R, P, C");
  });

  test("full address, no street name", () => {
    let addr = Object.assign({}, address);
    delete addr.streetName;
    expect(helper.addressToString(addr)).toEqual("S, CITY, R, P, C");
  });

  test("street and postcode missing", () => {
    let addr = Object.assign({}, address);
    delete addr.streetName;
    delete addr.streetNumber;
    delete addr.postcode;
    expect(helper.addressToString(addr)).toEqual("S, CITY, R, C");
  });

  test("only country", () => {
    expect(helper.addressToString({country: "C"})).toEqual("C");
  });

  test("only street", () => {
    expect(helper.addressToString(
        {streetNumber: "1", streetName: "STREET"})).toEqual("1 STREET");
  });

  test("only street, publicOnly", () => {
    expect(helper.addressToString({streetNumber: "1", streetName: "STREET"},
        true)).toEqual("");
  });

  test("all, publicOnly", () => {
    expect(helper.addressToString(address, true)).toEqual("S, CITY, R, C");
  });
});

describe("formatUserName", () => {
  const makeUser = (firstName = "FN", middleName = "MN", lastName = "LN",
      nickname = "NN") => ({
    firstName,
    middleName,
    lastName,
    nickname
  });

  test("all components given", () => {
    expect(helper.formatFullName(makeUser())).toEqual("FN MN LN (NN)");
  });

  test("nick name missing", () => {
    let user = makeUser();
    delete user.nickname;
    expect(helper.formatFullName(user)).toEqual("FN MN LN");
  });
  test("nick name null", () => {
    let user = makeUser();
    user.nickname = null;
    expect(helper.formatFullName(user)).toEqual("FN MN LN");
  });
  test("nick name empty", () => {
    let user = makeUser();
    user.nickname = "";
    expect(helper.formatFullName(user)).toEqual("FN MN LN");
  });
  test("nick name whitespace", () => {
    let user = makeUser();
    user.nickname = "  ";
    expect(helper.formatFullName(user)).toEqual("FN MN LN");
  });

  test("all components missing", () => {
    expect(helper.formatFullName({})).toEqual("");
  });

  test("first name missing", () => {
    let user = makeUser();
    delete user.firstName;
    expect(helper.formatFullName(user)).toEqual("MN LN (NN)");
  });

  test("middle name missing", () => {
    let user = makeUser();
    delete user.middleName;
    expect(helper.formatFullName(user)).toEqual("FN LN (NN)");
  });
});