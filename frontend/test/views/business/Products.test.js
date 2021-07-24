import Products from "@/views/business/Products";
import {mount} from "@vue/test-utils";
import {globalStateMocks} from "#/testHelper";

import {Api} from "@/Api";
import Vue from 'vue'
import Vuetify from 'vuetify'
import {ApiRequestError} from "@/ApiRequestError";

Vue.use(Vuetify);
let vuetify = new Vuetify();

jest.mock("@/Api");

let wrapper;

let response = {
    results: [
        {
            "id": 1,
            "creator": {},
            "section": "ForSale",
            "created": "2021-05-23T15:34:20+12:00",
            "displayPeriodEnd": "2021-06-23T15:34:20+12:00",
            "title": "title 1",
            "description": "description 1",
            "keywords": [1]
        },
        {
            "id": 2,
            "creator": {},
            "section": "ForSale",
            "created": "2021-05-24T15:34:20+12:00",
            "displayPeriodEnd": "2021-06-24T15:34:20+12:00",
            "title": "title 2",
            "description": "description 2",
            "keywords": [2]
        },
        {
            "id": 3,
            "creator": {},
            "section": "ForSale",
            "created": "2021-05-25T15:34:20+12:00",
            "displayPeriodEnd": "2021-06-25T15:34:20+12:00",
            "title": "title 3",
            "description": "description 3",
            "keywords": [3]
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
});

afterEach(() => wrapper.destroy());

describe("Product API handling", () => {
    /**
     * Tests that the products get set correctly.
     */
    test("Assert products get set from API", async () => {
        Api.getProducts.mockResolvedValue({
            data: response
        });
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