import {shallowMount} from "@vue/test-utils";
import ItemCard from "../components/cards/ItemCard";

const inventoryItem = {
    id: 1,
    product: {
        id: 2,
        name: 'beans',
        description: "Yummy beans",
    },
    quantity: 5,
    pricePerItem: 4,
    totalPrice: 20,
    manufactured: '2021-04-24',
    sellBy: null,
    bestBefore: null,
    expires: '2021-05-29',
}

describe("displays on the valid dates test", () => {
    test("valid dates displayed", () => {
        const wrapper = shallowMount(ItemCard, {
            propsData: {
                item: inventoryItem,
            }
        })
        expect(wrapper.text()).toContain('beans');
        expect(wrapper.text()).toContain('Yummy beans');
        expect(wrapper.text()).toContain('Manufactured On');
        expect(ItemCard.computed.metaValues.call({ item: inventoryItem })).toHaveLength(2);
    })
});