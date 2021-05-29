import {shallowMount} from "@vue/test-utils";
import Profile from "@/views/user/Detail";
import {globalStateMocks } from "#/testHelper";

jest.mock("@/Api");
import { Api } from "@/Api";

jest.useFakeTimers();

let wrapper;

window.alert = jest.fn();

beforeEach(() => {
  wrapper = shallowMount(Profile, {
    propsData: {
      userId: 20
    },
    mocks: {
      callApi: jest.fn(() => Promise.resolve({
        firstName: "returned user firstName",
        middleName: "returned user middleName",
        lastName: "returned user lastName",
        nickname: "returned user nickname",
        role: "returned user ROLE_ADMIN",
        email: "returned user email@example.com",
        dateOfBirth: "2000-01-01",
        homeAddress: {
          streetNumber: "returned user streetNumber",
          streetName: "returned user streetName",
          postcode: "returned user postcode",
          city: "returned user city",
          region: "returned user region",
          country: "returned user country",
        },
        phoneNumber: "returned user phoneNumber",
        bio: "returned user bio"
      })),
      ...globalStateMocks()
    },
    stubs: ["router-link"]
  });
});

afterEach(() => wrapper.destroy());

describe("Date string format", () => {
  test("standard jan", () => {
    expect(wrapper.vm.formatDate(new Date(2000, 0, 1))).toEqual(
        "1 January, 2000");
  });

  test("standard dec", () => {
    expect(wrapper.vm.formatDate(new Date(2000, 11, 31))).toEqual(
        "31 December, 2000");
  });

  test("ISO string", () => {
    expect(wrapper.vm.formatDate("2021-03-02T05:35:03")).toEqual(
        "2 March, 2021");
  });

  test("ISO string no time", () => {
    expect(wrapper.vm.formatDate("1949-05-09")).toEqual("9 May, 1949");
  });
});

describe("API response", () => {
  test("values being set", async () => {
    const data = {
      firstName: "F",
      middleName: "M",
      lastName: "L",
      nickname: "N",
      dateOfBirth: "2020-12-10",
      created: "2020-12-10T15:34:10",
      email: "example@example.com",
      phoneNumber: "bla",
      homeAddress: "10 Downing Street, Covent Garden, United Kingdom"
    }
    await wrapper.vm.parseApiResponse(Promise.resolve({data}));

    expect(wrapper.vm.userInfo).toEqual(data);
  });

  test("date of birth not given", async () => {
    await wrapper.vm.parseApiResponse(Promise.resolve({data: {}}));
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.dateOfBirthText).toEqual("Unknown");
  });

  test("date created not given", async () => {
    await wrapper.vm.parseApiResponse(Promise.resolve({data: {}}));
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.memberSinceText).toEqual("Unknown");
  });

  test("date of birth invalid", async () => {
    await wrapper.vm.parseApiResponse(Promise.resolve({
      data: {
        dateOfBirth: "2020-05-adsf"
      }
    }));
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.dateOfBirthText).toEqual("Unknown");
  });

  test("date created invalid", async () => {
    await wrapper.vm.parseApiResponse(Promise.resolve({
      data: {
        created: "abcd"
      }
    }));

    expect(wrapper.vm.memberSinceText).toEqual("Unknown");
  });

  test("date of birth valid", async () => {
    await wrapper.vm.parseApiResponse(Promise.resolve({
      data: {
        dateOfBirth: "2020-05-30"
      }
    }));
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.dateOfBirthText).toEqual("30 May, 2020");
  });

  test("date created text valid", async () => {
    await wrapper.vm.parseApiResponse(Promise.resolve({
      data: {
        created: "2010-04-20T01:15:03"
      }
    }));

    expect(wrapper.vm.memberSinceText).toEqual(
        `20 April, 2010 (${wrapper.vm.generateTimeSinceRegistrationText(
            new Date(2010, 3, 20), new Date()
        )})`
    );
  });
});

describe("months since registration", () => {
  test("0 months", () => {
    expect(wrapper.vm.generateTimeSinceRegistrationText(
        new Date(2020, 1, 1), new Date(2020, 1, 28)
    )).toEqual("0 months");
  });

  test("1 month", () => {
    expect(wrapper.vm.generateTimeSinceRegistrationText(
        new Date(2020, 1, 1), new Date(2020, 2, 1)
    )).toEqual("1 month");
  });

  test("5 months", () => {
    expect(wrapper.vm.generateTimeSinceRegistrationText(
        new Date(2020, 1, 1), new Date(2020, 6, 28)
    )).toEqual("5 months");
  });

  test("1 year", () => {
    expect(wrapper.vm.generateTimeSinceRegistrationText(
        new Date(2020, 1, 1), new Date(2021, 1, 28)
    )).toEqual("1 year, 0 months");
  });

  test("13 months", () => {
    expect(wrapper.vm.generateTimeSinceRegistrationText(
        new Date(2020, 1, 1), new Date(2021, 2, 28)
    )).toEqual("1 year, 1 month");
  });

  test("2 years and a few months", () => {
    expect(wrapper.vm.generateTimeSinceRegistrationText(
        new Date(2020, 1, 1), new Date(2022, 8, 28)
    )).toEqual("2 years, 7 months");
  });
});
