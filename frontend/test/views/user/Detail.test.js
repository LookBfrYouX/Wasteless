import {shallowMount} from "@vue/test-utils";
import Profile from "@/views/user/Detail";
import {globalStateMocks} from "#/testHelper";

jest.mock("@/Api");

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

  test("date of birth invalid", async () => {
    await wrapper.vm.parseApiResponse(Promise.resolve({
      data: {
        dateOfBirth: "2020-05-adsf"
      }
    }));
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.dateOfBirthText).toEqual("Unknown");
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
});


