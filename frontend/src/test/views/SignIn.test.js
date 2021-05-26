import {mount} from "@vue/test-utils";
import {globalStateMocks } from "@/test/testHelper";
import SignIn from "../../views/SignIn";
jest.mock("@/Api");
const { Api } = require("../../Api.js");

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
  wrapper = mount(SignIn, {
    mocks: globalStateMocks(),
    stubs: ["router-link"]
  });
});

window.HTMLElement.prototype.scrollIntoView = jest.fn();

afterEach(() => wrapper.destroy());

describe("testing sign in page", () => {
  test("testing sign in has been called on click", () => {
    wrapper.vm.signIn = jest.fn();

    jest.runAllTimers();

    wrapper.find("#submit").trigger("submit");
    expect(wrapper.vm.signIn).toHaveBeenCalled();
  });

  test("Api log in method called", async () => {
    Api.login.mockResolvedValue(successfulResponse());
    Api.profile.mockResolvedValue(wrapper.vm.$stateStore.getters.getAuthUser());
    await wrapper.vm.signIn();
    expect(Api.login.mock.calls.length).toBe(1);
  });

  test("unsuccessful sign in", async () => {
    wrapper.vm.signIn = jest.fn(() => {
      const data = successfulResponse();
      data.password = "notsignin";
      wrapper.setData(data)
      expect(wrapper.vm.responseSuccess).toBeFalsy();
    });
  });
});