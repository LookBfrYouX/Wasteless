import Products from "@/views/business/Products";
import {mount} from "@vue/test-utils";
import {globalStateMocks} from "#/testHelper";

import {Api} from "@/Api";
import Vuetify from 'vuetify'
import {ApiRequestError} from "@/ApiRequestError";

let vuetify = new Vuetify();

jest.mock("@/Api");

window.scrollTo = jest.fn();
let wrapper;

let response = {results: [
    {id: 1, name: "Product 1", description: "Description 1", manufacturer: "Manufacturer 1", recommendedRetailPrice: 4.67, created: "2020-07-14T14:32:00+12:00", primaryProductImage: null, images: []},
    {id: 2, name: "Product 2", description: "Description 2", manufacturer: "Manufacturer 2", recommendedRetailPrice: 3.0, created: "2020-07-14T14:32:00+12:00", primaryProductImage: null, images: []},
    {id: 3, name: "Product 3", description: "Description 3", manufacturer: "Manufacturer 3", recommendedRetailPrice: 3.3, created: "2020-07-14T14:32:00+12:00", primaryProductImage: null, images: []}
],
    totalCount: 3 }

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