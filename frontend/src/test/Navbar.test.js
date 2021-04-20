import {mount, shallowMount} from '@vue/test-utils';
import {globalStateMocks} from "./testHelper";
import Navbar from "../components/Navbar";


let wrapper;

afterEach(() => {
  wrapper.destroy();
});


describe("Entity name on Navbar", () => {
  test("Switch from individual to business", () => {
    const mocks = globalStateMocks();
    mocks.$stateStore.getters.getActingAs = jest.fn(() => null);
    wrapper = shallowMount(Navbar, {
      mocks,
    });
    expect(wrapper.vm.printCurrentActingAs).toEqual("firstName lastName");
  });
})
