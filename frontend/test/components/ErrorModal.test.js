import {mount} from "@vue/test-utils";
import Vuetify from 'vuetify'
import ErrorModal from "@/components/ErrorModal";

let vuetify = new Vuetify();

jest.mock("@/Api");


let wrapper;

const mountWithProps = props => {
  jest.clearAllMocks();
  wrapper = mount(ErrorModal, {
    vuetify,
    propsData: {
      title: "Test title",
      show: false,
      hideCallback: jest.fn(),
      refresh: true,
      retry: true,
      goBack: true, 
      ...props // at end so replaces defaults
    },
    mocks: {
      $router: {
        push: jest.fn(),
        go: jest.fn()
      }
    }
  });

  return wrapper;
}

afterEach(() => wrapper.destroy());

describe("refreshClicked", () => {
  test("refresh true", async () => {
    mountWithProps({
      refresh: true
    });
    
    await wrapper.vm.refreshClicked();
    expect(wrapper.vm.$router.go.mock.calls.length).toBe(1);
  });

  test("refresh false", async () => {
    mountWithProps({
      refresh: false 
    });
    
    await wrapper.vm.refreshClicked();
    expect(wrapper.vm.$router.go.mock.calls.length).toBe(0);
  });

  test("refresh callback", async () => {
    mountWithProps({
      refresh: jest.fn()
    });
   
    const someObject = {};
    await wrapper.vm.refreshClicked(someObject);
    expect(wrapper.vm.refresh.mock.calls.length).toBe(1);
    expect(wrapper.vm.refresh.mock.calls[0][0]).toBe(someObject);
  });
});


describe("retryClicked", () => {
  test("retry false", async () => {
    mountWithProps({
      retry: false 
    });
    
    await wrapper.vm.retryClicked(); // No error
  });

  test("retry callback", async () => {
    mountWithProps({
      retry: jest.fn()
    });
   
    const someObject = {};
    await wrapper.vm.retryClicked(someObject);
    expect(wrapper.vm.retry.mock.calls.length).toBe(1);
    expect(wrapper.vm.retry.mock.calls[0][0]).toBe(someObject);
  });
});


describe("goBackClicked", () => {
  test("go back true", async () => {
    mountWithProps({
      goBack: true
    });
    
    await wrapper.vm.goBackClicked();
    expect(wrapper.vm.$router.go.mock.calls.length).toBe(1);
    expect(wrapper.vm.$router.go.mock.calls[0][0]).toBe(-1);
  });

  test("go back false", async () => {
    mountWithProps({
      goBack: false 
    });
    
    await wrapper.vm.goBackClicked();
    expect(wrapper.vm.$router.go.mock.calls.length).toBe(0);
  });

  test("go back callback", async () => {
    mountWithProps({
      goBack: jest.fn()
    });
   
    const someObject = {};
    await wrapper.vm.goBackClicked(someObject);
    expect(wrapper.vm.goBack.mock.calls.length).toBe(1);
    expect(wrapper.vm.goBack.mock.calls[0][0]).toBe(someObject);
  });
});

