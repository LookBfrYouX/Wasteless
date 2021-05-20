import {shallowMount} from "@vue/test-utils";
import ItemCard from "../components/cards/InventoryItemCard";
import {globalStateMocks} from "./testHelper";

const inventoryItem = {
    id: 1,
    product: {
        id: 2,
        name: 'beans',
        description: "Yummy beans",
    },
    quantity: 5,
    pricePerItem: 4,
    totalPrice: '$20 NZD',
    manufactured: '2021-04-24',
    sellBy: null,
    bestBefore: null,
    expires: '2021-05-29',
    description: 'This bean is leftover from the International Bean Festival',
}

describe("displays on the valid dates test", () => {
    test("valid dates displayed", () => {
        let wrapper = shallowMount(ItemCard, {
            propsData: {
                item: inventoryItem,
                businessId: 0,
            },
            mocks: globalStateMocks(),
            stubs: ["router-link"]
        })
        expect(wrapper.props().item.product.name).toEqual('beans');
        expect(wrapper.props().item.product.description).toEqual('Yummy beans');
        expect(wrapper.text()).toContain('Manufactured On');
        expect(wrapper.props().item.description).toEqual('This bean is leftover from the International Bean Festival');
        // metaValue is a computer attribute that takes the four dates and only returns the list of those that
        // actually contain values (i.e. are not undefined, null, or empty strings)
        expect(ItemCard.computed.metaValues.call({ item: inventoryItem })).toHaveLength(2);
    })
});
