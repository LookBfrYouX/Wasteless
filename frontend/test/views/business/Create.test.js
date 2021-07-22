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
    const mockRegisterBusinessResponse = { data : {businessId: 1 } };
    wrapper = shallowMount(Create, {
      mocks,
    });
    Api.registerBusiness.mockResolvedValue(mockRegisterBusinessResponse);
    wrapper.vm.register();
    await wrapper.vm.$nextTick();
    expect(Api.registerBusiness.mock.calls.length).toBe(1);
  });

  test("user must be older than 16 to register (fail)", async () => {
    Api.registerBusiness.mock.calls.length = 0;
    let todayDate = new Date();
    let year = todayDate.getFullYear();
    let month = todayDate.getMonth();
    let day = todayDate.getDay();
    GLOBAL_STATE.authUser.dateOfBirth = year + "-" + month + "-" + day;
    const mocks = globalStateMocks();

    const mockRegisterBusinessResponse = { data : {businessId: 1 } };
    wrapper = shallowMount(Create, {
      mocks,
    });
    Api.registerBusiness.mockResolvedValue(mockRegisterBusinessResponse);
    wrapper.vm.register();
    await wrapper.vm.$nextTick();
    expect(Api.registerBusiness.mock.calls.length).toBe(0);
  });
});
