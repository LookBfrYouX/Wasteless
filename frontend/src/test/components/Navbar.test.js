import {shallowMount} from '@vue/test-utils';
import {GLOBAL_STATE, globalStateMocks } from "@/test/testHelper";
import Navbar from "../../components/Navbar";
import {ApiRequestError} from '../../ApiRequestError';

jest.mock("@/Api");
const {Api} = require("../../Api.js");

let wrapper;

afterEach(() => {
  wrapper.destroy();
});

describe("State of acting as entity", () => {
  test("Show user name on Navbar", () => {
    const mocks = globalStateMocks();
    mocks.$stateStore.getters.getActingAs = jest.fn(() => null);
    wrapper = shallowMount(Navbar, {
      mocks,
    });
    expect(wrapper.vm.printCurrentActingAs).toEqual("firstName lastName");
  });

  test("Show business name on Navbar", () => {
    const mocks = globalStateMocks();
    const business = GLOBAL_STATE.authUser.businessesAdministered[0];
    mocks.$stateStore.getters.getActingAs = jest.fn(() => business);
    mocks.$stateStore.actions.setActingAs();
    wrapper = shallowMount(Navbar, {
      mocks,
    });

    expect(wrapper.vm.printCurrentActingAs).toEqual(business.name);
  });

  test("Return a list of businesses", () => {
    const mocks = globalStateMocks();
    let authUser = mocks.$stateStore.getters.getAuthUser();
    let businessesAdministered = authUser.businessesAdministered;
    wrapper = shallowMount(Navbar, {
      mocks,
    });
    expect(wrapper.vm.actingAsEntities).toEqual(businessesAdministered);
  });
});

describe("sign out", () => {
  test("logout called", async () => {
    wrapper = shallowMount(Navbar, {
      mocks: globalStateMocks()
    });
    const logOut = jest.fn(() => Promise.resolve());
    Api.logOut.mockImplementation(logOut);
    await wrapper.vm.signOut();
    expect(logOut.mock.calls.length).toBe(1);
  });

  test("logout called", async () => {
    wrapper = shallowMount(Navbar, {
      mocks: globalStateMocks()
    });
    const logOut = jest.fn(() => Promise.reject(new ApiRequestError("MSG")));
    Api.logOut.mockImplementation(logOut);
    await wrapper.vm.signOut();
    expect(wrapper.vm.signOutErrorMessage).toBe("MSG");
  });
})
