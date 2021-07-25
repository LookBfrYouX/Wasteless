import Products from "@/views/business/Products";
import {mount} from "@vue/test-utils";
import {globalStateMocks} from "#/testHelper";

import {Api} from "@/Api";
import Vuetify from 'vuetify'
import {ApiRequestError} from "@/ApiRequestError";

let vuetify = new Vuetify();

jest.mock("@/Api");
window.scrollTo = jest.fn()

window.scrollTo = jest.fn();
let wrapper;

let response = {
    results: [
        {
            id: 1,
            creator: {},
            section: "ForSale",
            created: "2021-05-23T15:34:20+12:00",
            displayPeriodEnd: "2021-06-23T15:34:20+12:00",
            title: "title 1",
            description: "description 1",
            keywords: [1]
        },
        {
            id: 2,
            creator: {},
            section: "ForSale",
            created: "2021-05-24T15:34:20+12:00",
            displayPeriodEnd: "2021-06-24T15:34:20+12:00",
            title: "title 2",
            description: "description 2",
            keywords: [2]
        },
        {
            id: 3,
            creator: {},
            section: "ForSale",
            created: "2021-05-25T15:34:20+12:00",
            displayPeriodEnd: "2021-06-25T15:34:20+12:00",
            title: "title 3",
            description: "description 3",
            keywords: [3]
        }
    ],
    totalCount: 3
}

beforeEach(() => {
    wrapper = mount(Products, {
        vuetify,
        mocks: globalStateMocks(),
        stubs: ["error-modal", "router-link"], // Add the name of the business listings item component to here
        propsData: {
            businessId: 1
        }
    });
    Api.getProducts.mockResolvedValue({
        data: response
    });
});

afterEach(() => wrapper.destroy());

describe("Ensure values are set and changed", () => {
    /**
     * Tests the component is set up correctly.
     */
    test("Check data values are set", async () => {
        await wrapper.vm.pageUpdate();
        expect(wrapper.vm.$data.page).toEqual(1);
        expect(wrapper.vm.$data.totalResults).toEqual(3);
        expect(wrapper.vm.$data.searchParams).toBeDefined();
        expect(wrapper.vm.$data.searchParams.sortBy).toEqual("name");
    });

    test("Calling sortUpdate changes sort", async () => {
        await wrapper.vm.sortUpdate("recommendedRetailPrice", false);
        expect(wrapper.vm.$data.searchParams.sortBy).toEqual("recommendedRetailPrice")
        expect(wrapper.vm.$data.searchParams.isAscending).toBeFalsy();
    });
});

describe("Product API handling", () => {
    /**
     * Tests that the products get set correctly.
     */
    test("Assert products get set from API", async () => {
        await wrapper.vm.query();
        expect(wrapper.vm.$data.products).toEqual(response.results);

    });

    /**
     * Tests for the correct error when no data is returned.
     */
    test("API returns error", async () => {
        const message = "It's a Mario!";
        Api.getProducts.mockImplementation(
            () => Promise.reject(new ApiRequestError(message)));
        await wrapper.vm.query();
        expect(wrapper.vm.apiErrorMessage).toEqual(message);
    });
});