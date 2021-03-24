import { mount, shallowMount } from "@vue/test-utils";
import SignUp from "./SignUp";

jest.useFakeTimers();


const getData = () => {
  return {
    firstName: "FN",
    middleName: "MN",
    lastName: "LN",
    nickName: "NN",
    
    email: "example@example.com",
    
    password: "password",
    confirmPassword: "password",
    
    addressAsString: "10 Downing Street, Covent Garden, SW123, London, Greater London, United Kingdom",
    
    countryCode: 64,
    
    phoneNumber: "220230345",
    
    bio: "Every day is Mac Pro day",
  }
}

let wrapper;
beforeEach(() => {
  wrapper = mount(SignUp, {
    propsData: {},
    mocks: {},
    stubs: {
    }
  });
});

window.HTMLElement.prototype.scrollIntoView = jest.fn();


afterEach(() => wrapper.destroy());

describe("Sign up error handling", () => {
  test("passwords different", async () => {
    wrapper.vm.callApi = jest.fn();
    const data = getData();
    data.confirmPassword = "notpassword";
    wrapper.setData(data);
    await wrapper.vm.register();
    expect(wrapper.vm.callApi.mock.calls.length).toBe(0);
    expect(wrapper.vm.confirmPasswordWrong).toBeTruthy();
  });

  test("email used", async () => {
    wrapper.vm.$router = { push: jest.fn() };
    wrapper.vm.callApi = jest.fn(() => {
      return Promise.reject({
        response: {
          status: 409
        },
        userFacingErrorMessage: ""
      });
    });
    const data = getData();
    data.email = "notanemail";
    wrapper.setData(data);
    await wrapper.vm.register();
    wrapper.vm.$nextTick();
    expect(wrapper.vm.callApi.mock.calls.length).toBe(1);
    expect(wrapper.vm.emailUsed).toBeTruthy();
  });

  test("country code but not phone", async () => {
    wrapper.vm.callApi = jest.fn();
    const data = getData();
    data.phoneNumber = "";
    wrapper.setData(data);
    await wrapper.vm.register();
    wrapper.vm.$nextTick();
    expect(wrapper.vm.callApi.mock.calls.length).toBe(0);
    expect(wrapper.vm.phoneRequired).toBeTruthy();
  });

  test("phone but not country code", async () => {
    wrapper.vm.callApi = jest.fn();
    const data = getData();
    data.countryCode = null;
    wrapper.setData(data);
    await wrapper.vm.register();
    wrapper.vm.$nextTick();
    expect(wrapper.vm.callApi.mock.calls.length).toBe(0);
    expect(wrapper.vm.countryCodeRequired).toBeTruthy();
    expect(wrapper.vm.phoneRequired).toBeFalsy();
  });

  test("Error being cleared", async () => {
    wrapper.vm.callApi = jest.fn();
    const data = getData();
    data.confirmPassword = "notpassword";
    wrapper.setData(data);
    await wrapper.vm.register();

    data.confirmPassword = "password";
    data.phoneNumber = "";
    wrapper.setData(data);
    await wrapper.vm.register();
    expect(wrapper.vm.callApi.mock.calls.length).toBe(0);
    expect(wrapper.vm.confirmPasswordWrong).toBeFalsy();
    expect(wrapper.vm.phoneRequired).toBeTruthy();
  });
});
