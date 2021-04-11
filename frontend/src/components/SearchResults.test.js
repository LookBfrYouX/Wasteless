import {shallowMount} from "@vue/test-utils";
import {globalStateMocks} from "../../test/testHelper";
import SearchResults from "./SearchResults";

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

describe("parse search results", () => {
  test("empty string", () => {
    expect(wrapper.vm.parseSearchResults([{
      homeAddress: "",

    }])).toEqual([
      {
        region: "",
        city: "",
        country: "",
        homeAddress: ""
      }
    ])
  });
  test("no commas", () => {
    expect(wrapper.vm.parseSearchResults([{
      homeAddress: "33",

    }])).toEqual([
      {
        region: "",
        city: "",
        country: "",
        homeAddress: "33"
      }
    ])
  });

  test("one comma", () => {
    expect(wrapper.vm.parseSearchResults([{
      homeAddress: "33B Dovedale, Ilam",

    }])).toEqual([
      {
        region: "",
        city: "",
        country: "Ilam",
        homeAddress: "33B Dovedale, Ilam"
      }
    ])
  });

  test("two commas", () => {
    expect(wrapper.vm.parseSearchResults([{
      homeAddress: "2 Homestead Lane, Ilam, Christchurch",

    }])).toEqual([
      {
        region: "",
        city: "Ilam",
        country: "Christchurch",
        homeAddress: "2 Homestead Lane, Ilam, Christchurch"
      }
    ])
  });

  test("Three commas (Valid case)", () => {
    expect(wrapper.vm.parseSearchResults([{
      homeAddress: "2 Homestead Lane, Ilam, Christchurch, New Zealand",

    }])).toEqual([
      {
        region: "Ilam",
        city: "Christchurch",
        country: "New Zealand",
        homeAddress: "2 Homestead Lane, Ilam, Christchurch, New Zealand"
      }
    ])
  });

  test("Four commas", () => {
    expect(wrapper.vm.parseSearchResults([{
      homeAddress: "2 Homestead Lane, Ilam, Christchurch, New, Zealand",

    }])).toEqual([
      {
        region: "Christchurch",
        city: "New",
        country: "Zealand",
        homeAddress: "2 Homestead Lane, Ilam, Christchurch, New, Zealand"
      }
    ])
  });
})

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
