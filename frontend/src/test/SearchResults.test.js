import {shallowMount} from "@vue/test-utils";
import {globalStateMocks} from "./testHelper";
import SearchResults from "../components/SearchResults";

let wrapper;

window.alert = jest.fn();

beforeEach(() => {
  wrapper = shallowMount(SearchResults, {
    propsData: {
      search: "a"
    },
    mocks: {...globalStateMocks()},
  });
});

afterEach(() => wrapper.destroy());

describe("numPages", () => {
  test("No pages", () => {
    wrapper.setData({
      results: Array(0).fill({})
    });

    wrapper.vm.setPages();
    expect(wrapper.vm.pages).toEqual([]);
  });

  test("One page", () => {
    wrapper.setData({
      results: Array(1).fill({})
    });

    wrapper.vm.setPages();
    expect(wrapper.vm.pages).toEqual([0]);
  });

  test("10 pages", () => {
    wrapper.setData({
      results: Array(80).fill({})
    });

    wrapper.vm.setPages();
    expect(wrapper.vm.pages).toEqual([0, 1, 2, 3, 4, 5, 6, 7]);
  });

  test("Two pages", () => {
    wrapper.setData({
      results: Array(11).fill({})
    });

    wrapper.vm.setPages();
    expect(wrapper.vm.pages).toEqual([0, 1]);
  });

  test("One pages", () => {
    wrapper.setData({
      results: Array(9).fill({})
    });

    wrapper.vm.setPages();
    expect(wrapper.vm.pages).toEqual([0]);
  });
})

describe("sortedResults", () => {
  test("No sorting criteria", () => {
    wrapper.setData({
      sortBy: null
    });

    expect(wrapper.vm.sortedResults).toEqual(wrapper.vm.results);
  });

  test("Sorting different string", () => {
    wrapper.setData({
      reversed: false,
      sortBy: 'firstName',
      results: [
        {
          firstName: 'Alice',
          middleName: 'Eve',
          lastName: 'Smith',
          nickname: '',
          email: '',
          password: '',
          dateOfBirth: '',
          homeAddress: '',
          phoneNumber: '',
          bio: ''
        }, {
          firstName: 'Bob',
          middleName: 'Eve',
          lastName: 'Smith',
          nickname: '',
          email: '',
          password: '',
          dateOfBirth: '',
          homeAddress: '',
          phoneNumber: '',
          bio: ''
        }
      ]
    });

    expect(wrapper.vm.sortedResults).toEqual([
      {
        firstName: 'Alice',
        middleName: 'Eve',
        lastName: 'Smith',
        nickname: '',
        email: '',
        password: '',
        dateOfBirth: '',
        homeAddress: '',
        phoneNumber: '',
        bio: ''
      }, {
        firstName: 'Bob',
        middleName: 'Eve',
        lastName: 'Smith',
        nickname: '',
        email: '',
        password: '',
        dateOfBirth: '',
        homeAddress: '',
        phoneNumber: '',
        bio: ''
      }
    ]);
  });

  test("Reverse sorting different string", () => {
    wrapper.setData({
      reversed: true,
      sortBy: 'firstName',
      results: [
        {
          firstName: 'Alice',
          middleName: 'Eve',
          lastName: 'Smith',
          nickname: '',
          email: '',
          password: '',
          dateOfBirth: '',
          homeAddress: '',
          phoneNumber: '',
          bio: ''
        }, {
          firstName: 'Bob',
          middleName: 'Eve',
          lastName: 'Smith',
          nickname: '',
          email: '',
          password: '',
          dateOfBirth: '',
          homeAddress: '',
          phoneNumber: '',
          bio: ''
        }
      ]
    });

    expect(wrapper.vm.sortedResults).toEqual([
      {
        firstName: 'Bob',
        middleName: 'Eve',
        lastName: 'Smith',
        nickname: '',
        email: '',
        password: '',
        dateOfBirth: '',
        homeAddress: '',
        phoneNumber: '',
        bio: ''
      }, {
        firstName: 'Alice',
        middleName: 'Eve',
        lastName: 'Smith',
        nickname: '',
        email: '',
        password: '',
        dateOfBirth: '',
        homeAddress: '',
        phoneNumber: '',
        bio: ''
      }
    ]);
  });

  test("Sorting string and empty string", () => {
    wrapper.setData({
      reversed: false,
      sortBy: 'nickname',
      results: [
        {
          firstName: 'Alice',
          middleName: 'Eve',
          lastName: 'Smith',
          nickname: 'AA',
          email: '',
          password: '',
          dateOfBirth: '',
          homeAddress: '',
          phoneNumber: '',
          bio: ''
        }, {
          firstName: 'Bob',
          middleName: 'Eve',
          lastName: 'Smith',
          nickname: '',
          email: '',
          password: '',
          dateOfBirth: '',
          homeAddress: '',
          phoneNumber: '',
          bio: ''
        }
      ]
    });

    expect(wrapper.vm.sortedResults).toEqual([
      {
        firstName: 'Bob',
        middleName: 'Eve',
        lastName: 'Smith',
        nickname: '',
        email: '',
        password: '',
        dateOfBirth: '',
        homeAddress: '',
        phoneNumber: '',
        bio: ''
      }, {
        firstName: 'Alice',
        middleName: 'Eve',
        lastName: 'Smith',
        nickname: 'AA',
        email: '',
        password: '',
        dateOfBirth: '',
        homeAddress: '',
        phoneNumber: '',
        bio: ''
      }
    ]);
  });

  test("Reverse sorting string and empty string", () => {
    wrapper.setData({
      reversed: true,
      sortBy: 'nickname',
      results: [
        {
          firstName: 'Alice',
          middleName: 'Eve',
          lastName: 'Smith',
          nickname: 'AA',
          email: '',
          password: '',
          dateOfBirth: '',
          homeAddress: '',
          phoneNumber: '',
          bio: ''
        }, {
          firstName: 'Bob',
          middleName: 'Eve',
          lastName: 'Smith',
          nickname: '',
          email: '',
          password: '',
          dateOfBirth: '',
          homeAddress: '',
          phoneNumber: '',
          bio: ''
        }
      ]
    });

    expect(wrapper.vm.sortedResults).toEqual([
      {
        firstName: 'Alice',
        middleName: 'Eve',
        lastName: 'Smith',
        nickname: 'AA',
        email: '',
        password: '',
        dateOfBirth: '',
        homeAddress: '',
        phoneNumber: '',
        bio: ''
      }, {
        firstName: 'Bob',
        middleName: 'Eve',
        lastName: 'Smith',
        nickname: '',
        email: '',
        password: '',
        dateOfBirth: '',
        homeAddress: '',
        phoneNumber: '',
        bio: ''
      }
    ]);
  });
})
