import {mount, shallowMount} from '@vue/test-utils';
import {globalStateMocks} from "./testHelper";
import Navbar from "../components/Navbar";


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
    let authUser = mocks.$stateStore.getters.getAuthUser();
    mocks.$stateStore.actions.setActingAs(authUser.businesses[0]);
    wrapper = shallowMount(Navbar, {
      mocks,
    });
    expect(wrapper.vm.printCurrentActingAs).toEqual("TestName");
  });

  test("Switch from individual to business", () => {
    const mocks = globalStateMocks();
    let authUser = mocks.$stateStore.getters.getAuthUser();
    let business = authUser.businesses[0]
    wrapper = shallowMount(Navbar, {
      mocks,
    });
    wrapper.vm.switchActingAs(business);
    expect(mocks.$stateStore.getters.getActingAs()).toEqual(business);
  })

  test("Switch from business to individual", () => {
    const mocks = globalStateMocks();
    let authUser = mocks.$stateStore.getters.getAuthUser();
    mocks.$stateStore.actions.setActingAs(authUser.businesses[0]);
    wrapper = shallowMount(Navbar, {
      mocks,
    });
    wrapper.vm.switchActingAs();
    expect(mocks.$stateStore.getters.getActingAs()).toEqual(null);
  });

  test("Return a list of businesses", () => {
    const mocks = globalStateMocks();
    let authUser = mocks.$stateStore.getters.getAuthUser();
    let businesses = authUser.businesses;
    wrapper = shallowMount(Navbar, {
      mocks,
    });
    expect(wrapper.vm.actingAsEntities).toEqual(businesses);
  });
});
