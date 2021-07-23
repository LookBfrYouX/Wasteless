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
      nickName: "ID 1"
    },
    {
      id: 2,
      firstName: "Secondary",
      middleName: "Business",
      lastName: "Admin",
      nickName: "ID 2"
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

describe("existingAdminIds", () => {
  test("two admins", async () => {
    expect(wrapper.vm.existingAdminIds()).toEqual(new Set([1, 2]));
  });

  test("businesses not fetched yet", async () => {
    wrapper.vm.business = null;
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.existingAdminIds().size()).toEqual(0);
  });
});

describe("admins", () => {
  test("two admins", async () => {
    expect(wrapper.vm.admins()).toEqual([{
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
    expect(wrapper.vm.admins()).toEqual(null);
  });
});
