import {mount} from "@vue/test-utils";
import {globalStateMocks} from "./testHelper";
import Login from "../components/Login";

let wrapper;

jest.useFakeTimers();

const successfulResponse = () => {
  // constant for mocking a successful response
  return {
    email: "username@user.com",
    password: "LOGIN",
  };
};

beforeEach(() => {
  wrapper = mount(Login, {
    mocks: {...globalStateMocks()}
  });
});

window.HTMLElement.prototype.scrollIntoView = jest.fn();

afterEach(() => wrapper.destroy());

describe("testing login page", () => {
  test("checking signUp has been called on click", () => {
    wrapper.vm.signUp = jest.fn();

    jest.runAllTimers();

    wrapper.find("#signUp").trigger("click");
    expect(wrapper.vm.signUp).toHaveBeenCalled();
  });

  test("testing login has been called on click", () => {
    wrapper.vm.login = jest.fn();

    jest.runAllTimers();

    wrapper.find("#loginSubmit").trigger("submit");
    expect(wrapper.vm.login).toHaveBeenCalled();
  });

  test("successful login", async () => {
    wrapper.vm.login = jest.fn(() => {
      const data = successfulResponse();
      wrapper.setData(data)
      expect(wrapper.vm.responseSuccess).toBeTruthy();
    });
  });
  test("unsuccessful login", async () => {
    wrapper.vm.login = jest.fn(() => {
      const data = successfulResponse();
      data.password = "notlogin";
      wrapper.setData(data)
      expect(wrapper.vm.responseSuccess).toBeFalsy();
    });
  });
});
