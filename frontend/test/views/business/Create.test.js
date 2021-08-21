import {shallowMount} from "@vue/test-utils";
import {GLOBAL_STATE, globalStateMocks} from "#/testHelper";
import Create from "@/views/business/Create";

jest.mock("@/Api");
const {Api} = require("@/Api.js");

jest.useFakeTimers();
let wrapper;
window.alert = jest.fn();

afterEach(() => wrapper.destroy());

describe("Register business", () => {
  test("user must be older than 16 to register (pass)", async () => {
    Api.registerBusiness.mock.calls.length = 0;
    const mocks = globalStateMocks();
    const mockRegisterBusinessResponse = {data: {businessId: 1}};
    wrapper = shallowMount(Create, {
      mocks,
      props: {
        userId: 100
      }
    });
    Api.registerBusiness.mockResolvedValue(mockRegisterBusinessResponse);
    wrapper.vm.register();
    await wrapper.vm.$nextTick();
    expect(Api.registerBusiness.mock.calls.length).toBe(1);
  });

  test("user must be older than 16 to register (fail)", async () => {
    Api.registerBusiness.mock.calls.length = 0;
    const today = new Date();
    let dateOfBirth = new Date(
        today.getFullYear() - 15,
        today.getMonth(),
        today.getDay()
    ).toISOString().substring(0, 10); // Might be off by a day due to timezones, but doesn't really matter
    GLOBAL_STATE.authUser.dateOfBirth = dateOfBirth;
    const mocks = globalStateMocks();

    const mockRegisterBusinessResponse = {data: {businessId: 1}};
    wrapper = shallowMount(Create, {
      mocks,
      props: {
        userId: 20
      }
    });
    Api.registerBusiness.mockResolvedValue(mockRegisterBusinessResponse);
    wrapper.vm.register();
    await wrapper.vm.$nextTick();
    expect(Api.registerBusiness.mock.calls.length).toBe(0);
  });
});
