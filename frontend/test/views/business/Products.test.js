import Products from "@/views/business/Products";
import {mount} from "@vue/test-utils";
import {globalStateMocks} from "#/testHelper";

import {Api} from "@/Api";
import Vue from 'vue'
import Vuetify from 'vuetify'

Vue.use(Vuetify);
let vuetify = new Vuetify();

jest.mock("@/Api");

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
    test("Does it get to here", () => {
        console.log("Yes it does!");
        Api.getProducts.mockResolvedValue({
            data: response
        });
        await wrapper.vm.query();
        console.log();
    });
});