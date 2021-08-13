import {shallowMount} from "@vue/test-utils";
import ListingDetail from "@/views/business/ListingDetail";
import {globalStateMocks} from "#/testHelper";
import {Api} from "@/Api";

jest.mock("@/Api");
let wrapper;

beforeEach(() => {
    wrapper = shallowMount(ListingDetail, {
        propsData: {
            businessId: 1,
            listingId: 1
        },
        stubs: ['router-link'],
        mocks: {
            ...globalStateMocks()
        }
    });
});

afterEach(() => wrapper.destroy());

describe("Test currencies", () => {
    test("Fail Not Signed In", async () => {
        wrapper.vm.$stateStore.getters.isSignedIn = () => false;

        expect(await wrapper.vm.loadCurrencies()).toEqual(false);
    }),

    test("Fail, expect 'return;'", async () => {
        wrapper.vm.$stateStore.getters.isSignedIn = () => true;
        wrapper.vm.$helper.getCurrencyForBusiness = () => {throw new Error()};
        Api.handle401.call = () => true;

        // Nothing returned
        expect(await wrapper.vm.loadCurrencies()).toEqual(undefined);
    }),

    test("Fail, expect Api handle fails", async () => {
        wrapper.vm.$stateStore.getters.isSignedIn = () => true;
        wrapper.vm.$helper.getCurrencyForBusiness = () => {throw new Error()};
        Api.handle401.call = () => false;

        expect(await wrapper.vm.loadCurrencies()).toEqual(false);
    }),

    test("Success", async () => {
        const mockCurrency = "NZD";
        wrapper.vm.$stateStore.getters.isSignedIn = () => true;
        wrapper.vm.$helper.getCurrencyForBusiness = () => mockCurrency;

        expect(await wrapper.vm.loadCurrencies()).toEqual(true);
        // await wrapper.vm.$nextTick();
        expect(wrapper.vm.$data.currency).toEqual(mockCurrency)
    });
});
