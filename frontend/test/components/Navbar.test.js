import {shallowMount} from '@vue/test-utils';
import {GLOBAL_STATE, globalStateMocks} from "#/testHelper";
import Navbar from "@/components/Navbar";
import {ApiRequestError} from "@/ApiRequestError";

jest.mock("@/Api");
const {Api} = require("@/Api.js");

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
      stubs: ["router-link"] 
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
      stubs: ["router-link"] 
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
      mocks: globalStateMocks(),
      stubs: ["router-link"] 
    });
    const logOut = jest.fn(() => Promise.resolve());
    Api.logOut.mockImplementation(logOut);
    await wrapper.vm.signOut();
    expect(logOut.mock.calls.length).toBe(1);
  });

  test("logout called", async () => {
    wrapper = shallowMount(Navbar, {
      mocks: globalStateMocks(),
      stubs: ["router-link"]
    });
    const logOut = jest.fn(() => Promise.reject(new ApiRequestError("MSG")));
    Api.logOut.mockImplementation(logOut);
    await wrapper.vm.signOut();
    expect(wrapper.vm.signOutErrorMessage).toBe("MSG");
  });
})

describe("Search functionality", () => {
  test("Same page, refresh", async () => {
    const query = "";
    const searchName = "Search";

    wrapper = shallowMount(Navbar, {
      mocks: globalStateMocks(),
      propsData: {
        query: query,
      },
      stubs: ["router-link"]
    });

    wrapper.vm.$route.name = searchName;
    wrapper.vm.$route.params.query = query;

    await wrapper.vm.search();
    expect(wrapper.vm.$router.go).toBeCalledTimes(1);
    expect(wrapper.vm.$router.push).toBeCalledTimes(0);
  });

  test("Different page, push", async () => {
    const query1 = "Something";
    const query2 = "Hello";
    const searchName = "Search";

    wrapper = shallowMount(Navbar, {
      mocks: globalStateMocks(),
      propsData: {
        query: query1,
      },
      stubs: ["router-link"]
    });

    wrapper.vm.$route.name = searchName;
    wrapper.vm.$route.params.query = query2;

    await wrapper.vm.search();
    expect(wrapper.vm.$router.go).toBeCalledTimes(0);
    expect(wrapper.vm.$router.push).toBeCalledTimes(1);
  });
})

describe("pushOrGoToBusinessPage functionality", () => {
  test("Acting as business, user already on same page", async () => {
    const routeName = "CurrentPage";
    wrapper = shallowMount(Navbar, {
      mocks: globalStateMocks(),
      stubs: ["router-link"]
    });

    wrapper.vm.$route.name = routeName;
    wrapper.vm.$route.params.businessId = GLOBAL_STATE.authUser.businessesAdministered[0].id;

    await wrapper.vm.pushOrGoToBusinessPage(routeName);
    expect(wrapper.vm.$router.go).toBeCalledTimes(1);
    expect(wrapper.vm.$router.push).toBeCalledTimes(0);
  });

  test("Acting as business, user already on same page", async () => {
    const routeName = "CurrentPage";
    wrapper = shallowMount(Navbar, {
      mocks: globalStateMocks(),
      stubs: ["router-link"]
    });
    wrapper.vm.$router.go.mock.calls.length = 0;
    wrapper.vm.$router.push.mock.calls.length = 0;
    wrapper.vm.$route.name = routeName;
    wrapper.vm.$route.params.businessId = GLOBAL_STATE.authUser.businessesAdministered[0].id + "1";

    await wrapper.vm.pushOrGoToBusinessPage(routeName);
    expect(wrapper.vm.$router.go).toBeCalledTimes(0);
    expect(wrapper.vm.$router.push).toBeCalledTimes(1);
  });

  test("Not acting as business", async () => {
    wrapper = shallowMount(Navbar, {
      mocks: globalStateMocks(),
      stubs: ["router-link"]
    });
    wrapper.vm.$stateStore.getters.getActingAs = () => null;

    await wrapper.vm.pushOrGoToBusinessPage("Any Page");
    expect(wrapper.vm.$router.go).toBeCalledTimes(0);
    expect(wrapper.vm.$router.push).toBeCalledTimes(0);
  });
})