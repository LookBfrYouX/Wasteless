import {shallowMount} from "@vue/test-utils";
import BusinessProfile from "@/views/business/Detail";
import {globalStateMocks} from "#/testHelper";

jest.useFakeTimers();

let wrapper;

window.alert = jest.fn();

beforeEach(() => {
  wrapper = shallowMount(BusinessProfile, {
    propsData: {
      businessId: 20
    },
    mocks: {
      callApi: jest.fn(() => Promise.resolve({
        name: "returned business name",
        description: "returned business description",
        businessType: "returned business type",
        address: {
          streetNumber: "returned business streetNumber",
          streetName: "returned business streetName",
          postcode: "returned business postcode",
          city: "returned business city",
          region: "returned business region",
          country: "returned business country",
        },
      })),
      ...globalStateMocks()
    },
    stubs: ["router-link"]
  });
});

afterEach(() => wrapper.destroy());

describe("API response", () => {
  test("values for business information being set", async () => {
    const data = {
      name: "Bargains",
      description: "This is a lovely description for Bargain's",
      businessType: "Retail Trade",
      address: "10 Downing Street, Covent Garden, United Kingdom"
    }
    await wrapper.vm.parseApiResponse(Promise.resolve({data}));
    expect(wrapper.vm.businessInfo).toEqual(data);
  });
  test("values for business information being set", async () => {
    const data = {
      name: "",
      description: "This is a lovely description for Bargain's",
      businessType: "Retail Trade",
      address: "10 Downing Street, Covent Garden, United Kingdom"
    }
    await wrapper.vm.parseApiResponse(Promise.reject({data}));
    expect(wrapper.vm.businessInfo).not.toEqual(data);
  });
});

describe("adminLinks", () => {
  const method = BusinessProfile.computed.adminLinks;
  // Probably a bit faster to run tests this way; bind this and get return value
  // of method instead of using wrapper.vm.$nextTick();

  const checker = (names, output) => expect(method.call({
    businessInfo: {
      administrators: names.map((name, id) => ({ firstName: name, id })) 
    },
    $helper: { formatFullName: ({ firstName }) => firstName }
  }).map(el => el.text).join("")).toEqual(output);

 
  test("One user", () => checker(["A"], "A"));
  test("Two users", () => checker(["A", "B"], "A and B"));
  test("Three users", () => checker(["A", "B", "C"], "A, B and C"));
  test("Five users", () => checker(["A", "B", "C", "D", "E"], "A, B, C, D and E"));
  test("No users", () => checker([""], ""));

  test("Business not set", () => {
    expect(method.call({businessInfo: null})).toEqual([]);
  });


  test("Full mounted test", async () => {
    wrapper.vm.businessInfo = {
      administrators: [
        { firstName: "A", lastName: "B", id: 2 },
        { firstName: "C", lastName: "D", id: 3 },
        { firstName: "E", lastName: "F", nickname: "G", id: 4 },
      ],
      address: {}
    };

    await wrapper.vm.$nextTick();
    expect(wrapper.vm.adminLinks).toEqual([
      { text: "A B, ", userId: 2 },
      { text: "C D", userId: 3 },
      { text: " and E F (G)", userId: 4 }
    ]);
  });
});
