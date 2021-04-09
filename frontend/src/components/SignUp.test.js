import { mount, shallowMount } from "@vue/test-utils";
import { ApiRequestError } from "../ApiRequestError";
import SignUp from "./SignUp";

jest.useFakeTimers();


const getData = () => {
  return {
    firstName: "FN",
    middleName: "MN",
    lastName: "LN",
    nickName: "NN",
    
    email: "example@example.com",
    
    password: "Passw0rd",
    confirmPassword: "Passw0rd",

    dateOfBirth: "2000-01-01",
    
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

describe("Date of birth", () => {
  const now = new Date(2020, 1, 22); // 2020-02-22
  test("Too young by years", () => {
      expect(wrapper.vm.validateDateOfBirth("2010-03-05", now)).toBeTruthy();
  });
  test("Too young by months", () => {
    expect(wrapper.vm.validateDateOfBirth("2007-03-05", now)).toBeTruthy();
  });
  test("Too young by days", () => {
    expect(wrapper.vm.validateDateOfBirth("2007-02-24", now)).toBeTruthy();
  });
  test("13th birthday", () => {
    expect(wrapper.vm.validateDateOfBirth("2007-02-22", now)).toBeFalsy();
  });
  test("same year old enough", () => {
    expect(wrapper.vm.validateDateOfBirth("2007-01-22", now)).toBeFalsy();
  });
  test("bad format", () => {
    expect(wrapper.vm.validateDateOfBirth("blaaa", now)).toBeTruthy();
  });
  test("bad format", () => {
    expect(wrapper.vm.validateDateOfBirth(undefined, now)).toBeTruthy();
  });
  test("bad format", () => {
    expect(wrapper.vm.validateDateOfBirth("20202-53-52", now)).toBeTruthy();
  });
  test("bad date", () => {
    expect(wrapper.vm.validateDateOfBirth("2001-02-29", now)).toBeTruthy();
  });
});

describe("Sign up error handling", () => {
  test("passwords different", async () => {
    wrapper.vm.callApi = jest.fn();
    const data = getData();
    data.confirmPassword = "notpassword";
    wrapper.setData(data);
    await wrapper.vm.register();
    expect(wrapper.vm.callApi.mock.calls.length).toBe(0);
    expect(wrapper.vm.confirmPasswordErrorMessage).toBeTruthy();
  });

  test("email used", async () => {
    wrapper.vm.$router = { push: jest.fn() };
    wrapper.vm.callApi = jest.fn(() => {
      const error = new ApiRequestError("Some error message");
      error.status = 409;
      return Promise.reject(error);
    });
    const data = getData();
    data.email = "notanemail";
    wrapper.setData(data);
    await wrapper.vm.register();
    wrapper.vm.$nextTick();
    // only way to find out if email is used is by calling the API
    expect(wrapper.vm.callApi.mock.calls.length).toBe(1);
    expect(wrapper.vm.emailErrorMessage).toBeTruthy();
  });

  test("country code but not phone", async () => {
    wrapper.vm.callApi = jest.fn();
    const data = getData();
    data.phoneNumber = "";
    wrapper.setData(data);
    await wrapper.vm.register();
    wrapper.vm.$nextTick();
    expect(wrapper.vm.callApi.mock.calls.length).toBe(0);
    expect(wrapper.vm.phoneErrorMessage).toBeTruthy();
  });

  test("phone but not country code", async () => {
    wrapper.vm.callApi = jest.fn();
    const data = getData();
    data.countryCode = null;
    wrapper.setData(data);
    await wrapper.vm.register();
    wrapper.vm.$nextTick();
    expect(wrapper.vm.callApi.mock.calls.length).toBe(0);
    expect(wrapper.vm.countryCodeErrorMessage).toBeTruthy();
    expect(wrapper.vm.phoneErrorMessage).toBeFalsy();
  });

  test("Error being cleared", async () => {
    wrapper.vm.callApi = jest.fn();
    const data = getData();
    data.confirmPassword = "notpassword";
    wrapper.setData(data);
    await wrapper.vm.register();

    data.confirmPassword = data.password;
    data.phoneNumber = "";
    wrapper.setData(data);
    await wrapper.vm.register();
    expect(wrapper.vm.callApi.mock.calls.length).toBe(0);
    
    console.log(wrapper.vm.errorMessage);
    expect(wrapper.vm.errorMessage).toBeTruthy();
    expect(wrapper.vm.phoneErrorMessage).toBeTruthy();
    //Ensures all the error messages are cleared
    expect(wrapper.vm.countryCodeErrorMessage).toBeFalsy();
    expect(wrapper.vm.confirmPasswordWrong).toBeFalsy();
    expect(wrapper.vm.emailErrorMessage).toBeFalsy();
    expect(wrapper.vm.dateOfBirthErrorMessage).toBeFalsy();
  });
});
