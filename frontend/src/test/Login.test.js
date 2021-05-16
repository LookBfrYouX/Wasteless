import {mount} from "@vue/test-utils";
import {globalStateMocks} from "./testHelper";
import Login from "../components/Login";
jest.mock("../Api.js");
const { Api } = require("../Api.js");

let wrapper;

jest.useFakeTimers();


const validRequest = () => {
  return {
    email: "request@user.com",
    password: "PASS",
  };
};

const successfulResponse = () => {
  // constant for mocking a successful response
  return {
    data: {
      userId: 101
    }
  };
};

const mock = globalStateMocks();

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

  test("Api login method called", async () => {
    Api.login.mockResolvedValue(successfulResponse());
    Api.profile.mockResolvedValue(wrapper.vm.$stateStore.getters.getAuthUser());
    await wrapper.vm.login();
    expect(Api.login.mock.calls.length).toBe(1);
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

test("Routed to Sign up page", async () => {
  wrapper.vm.signUp();
  await wrapper.vm.$nextTick();
  // console.log(wrapper.vm.$router.push.mock.calls[0][0].name);
  expect(wrapper.vm.$router.push.mock.calls[0][0].name).toEqual("signUp");
});