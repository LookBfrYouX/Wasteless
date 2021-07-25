import {shallowMount, mount} from "@vue/test-utils";
import AdminEdit from "@/views/business/AdminEdit";
import {globalStateMocks} from "#/testHelper";

jest.mock("@/Api");
import {Api} from "@/Api";

import Vue from 'vue'
import Vuetify from 'vuetify'
Vue.use(Vuetify);
const vuetify = new Vuetify();

import {ApiRequestError} from "@/ApiRequestError";

const BUSINESS_TEMPLATE = {
  id: 1,
  name: "Business Name",
  primaryAdministratorId: 1,
  administrators: [
    {
      id: 1,
      firstName: "Primary",
      middleName: "Business",
      lastName: "Admin",
      nickname: "ID 1"
    },
    {
      id: 2,
      firstName: "Secondary",
      middleName: "Business",
      lastName: "Admin",
      nickname: "ID 2"
    }
  ]
};
Object.freeze(BUSINESS_TEMPLATE);

let business;
let wrapper;

const standardMount = () => {
  wrapper = mount(AdminEdit, {
    vuetify,
    mocks: globalStateMocks(),
    stubs: ["error-modal", "router-link", "v-autocomplete", "v-data-table"], // Add the name of the business listings item component to here
    propsData: {
        businessId: business.id
    }
  });

  return wrapper;
}

beforeEach(() => {
  business = Object.assign({}, BUSINESS_TEMPLATE);
  Api.businessProfile.mockResolvedValue({
    data: business
  });
  standardMount();
});

afterEach(() => {
  wrapper.destroy();
  jest.clearAllMocks();
});


describe("fetchBusiness", () => {
  test("successful mount", async () => {
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.business).toEqual(business);
  });

  test("failed mount", async () => {
    Api.businessProfile.mockImplementation(() => Promise.reject(new ApiRequestError("Err")));
    standardMount();
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.business).toBe(null);
    expect(wrapper.vm.apiErrorMessage).toEqual("Err");
  });
});

describe("addAdmin", () => {
  test("success", async () => {
    wrapper.vm.adminIdToAdd = 3;
    Api.addBusinessAdmin.mockResolvedValue();
    await wrapper.vm.addAdmin();
    expect(Api.addBusinessAdmin.mock.calls.length).toEqual(1);
    expect(Api.addBusinessAdmin.mock.calls[0]).toEqual([business.id, 3]); // businessId, user Id
    expect(Api.businessProfile.mock.calls.length).toEqual(2); // business fetched again to get new admin
  });


  test("failed API call", async () => {
    wrapper.vm.adminIdToAdd = 3;
    Api.addBusinessAdmin.mockImplementation(() => Promise.reject(new ApiRequestError("Err")));
    await wrapper.vm.addAdmin();
    expect(Api.addBusinessAdmin.mock.calls.length).toEqual(1);
    expect(wrapper.vm.addAdminErrorMessage).toEqual("Err");
    expect(Api.businessProfile.mock.calls.length).toEqual(1); // business not fetched again
  });
});

describe("removeAdmin", () => {
  test("success", async () => {
    wrapper.vm.adminIdToRemove= 3;
    Api.removeBusinessAdmin.mockResolvedValue();
    await wrapper.vm.removeAdmin();
    expect(Api.removeBusinessAdmin.mock.calls.length).toEqual(1);
    expect(Api.removeBusinessAdmin.mock.calls[0]).toEqual([business.id, 3]); // businessId, user Id
    expect(Api.businessProfile.mock.calls.length).toEqual(2); // business fetched again to get new admin
  });


  test("failed API call", async () => {
    wrapper.vm.adminIdToRemove = 3;
    Api.removeBusinessAdmin.mockImplementation(() => Promise.reject(new ApiRequestError("Err")));
    await wrapper.vm.removeAdmin();
    expect(Api.removeBusinessAdmin.mock.calls.length).toEqual(1);
    expect(wrapper.vm.removeAdminErrorMessage).toEqual("Err");
    expect(Api.businessProfile.mock.calls.length).toEqual(1); // business not fetched again
  });
});

describe("existingAdminIds", () => {
  test("two admins", async () => {
    expect(wrapper.vm.existingAdminIds).toEqual(new Set([1, 2]));
  });

  test("businesses not fetched yet", async () => {
    wrapper.vm.business = null;
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.existingAdminIds.size).toEqual(0);
  });
});

describe("admins", () => {
  test("two admins", async () => {
    expect(wrapper.vm.admins).toEqual([{
      name: "Primary Business Admin (ID 1)",
      id: 1
    }, {
      name: "Secondary Business Admin (ID 2)",
      id: 2
    }]);
  });

  test("businesses not fetched yet", async () => {
    wrapper.vm.business = null;
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.admins).toEqual(null);
  });
});

describe("userCanModifyBusiness", () => {
  test("GAA", async () => {
    wrapper.vm.$stateStore.getters.isAdmin = () => true;
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.userCanModifyBusiness).toEqual(true);
  });


  test("Primary business admin", async () => {
    wrapper.vm.$stateStore.getters.isAdmin = () => false;
    wrapper.vm.$stateStore.getters.getAuthUser = () => ({ id: 4 });
    wrapper.vm.business.primaryAdministratorId = 4;
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.userCanModifyBusiness).toEqual(true);
  });

  test("Not primary business admin or GAA", async () => {
    wrapper.vm.$stateStore.getters.isAdmin = () => false;
    wrapper.vm.$stateStore.getters.getAuthUser = () => ({ id: 4 });
    wrapper.vm.business.primaryAdministratorId = 3;
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.userCanModifyBusiness).toEqual(false);
  });
});

describe("userSearchResults", () => {
  test("two admins plus another user", async () => {
    wrapper.vm.$data.userSearchResultsRaw = [
        ...business.administrators,
      {
        id: 3,
        firstName: "A",
        middleName: "nother",
        lastName: "User"
      }
    ];
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.userSearchResults).toEqual([{
      name: "Primary Business Admin (ID 1)",
      id: 1,
      disabled: true
    }, {
      name: "Secondary Business Admin (ID 2)",
      id: 2,
      disabled: true
    }, {
      name: "A nother User",
      id: 3,
      disabled: false
    }]);
  });

  test("businesses not fetched yet", async () => {
    expect(wrapper.vm.userSearchResults).toEqual([]);
  });

  test("disabled property in results", async () => {
    wrapper.vm.$data.userSearchResultsRaw = [
      {id: 100, disabled: true, firstName: "A"},
      {id: 200, disabled: false, firstName: "B"}
    ]
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.userSearchResults).toEqual([
      {id: 100, name: "A", disabled: true},
      {id: 200, name: "B", disabled: false},
    ]);
  });
});

describe("userSearchQuery", () => {
    const results = [
      {id: 3, firstName: "A", lastName: "B"},
      {id: 4, firstName: "C", lastName: "D"}
    ];

  test("standard query, check userSearchResultsSet", async () => {
    Api.search.mockResolvedValue({ data: { results }});
    
    await wrapper.setData({
      userSearchQuery: "test"
    });

    await wrapper.vm.$nextTick();
    expect(wrapper.vm.userSearchResultsRaw).toEqual(results);
  });

  test("disabling submit button", async () => {
    // if user selects a result and then types some other stuff, the adminIdToAdd
    // should be set to null
    Api.search.mockResolvedValue({ data: { results }});
    
    await wrapper.setData({
      adminIdToAdd: 3,
      userSearchQuery: "A B",
      userSearchResultsRaw: results
    });
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.adminIdToAdd).toEqual(3);

    await wrapper.setData({
      userSearchQuery: "A B C"
    });
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.adminIdToAdd).toBe(null);
  });


  test("user search loading is set and reset after API loads; successful API call", async () => {
    const wait = (duration) => new Promise(resolve => setTimeout(resolve, duration));
    const time = 100;
    Api.search.mockImplementation(async () => {
      await wait(time);
      return { data: { results }};
    });
    await wrapper.setData({ userSearchQuery: "asdf" });
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.userSearchLoading).toBe(true);
    await wait(time);
    expect(wrapper.vm.userSearchLoading).toBe(false);
  });

  test("user search loading is set and reset after API loads: failed API call", async () => {
    const wait = (duration) => new Promise(resolve => setTimeout(resolve, duration));
    const time = 100;
    Api.search.mockImplementation(async () => {
      await wait(time);
      return Promise.reject();
    });
    await wrapper.setData({ userSearchQuery: "asdf" });
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.userSearchLoading).toBe(true);
    await wait(time);
    expect(wrapper.vm.userSearchLoading).toBe(false);
  });
});