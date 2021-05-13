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
    totalPrice: '$20 NZD',
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
        // metaValue is a computer attribute that takes the four dates and only returns the list of those that
        // actually contain values (i.e. are not undefined, null, or empty strings)
        expect(ItemCard.computed.metaValues.call({ item: inventoryItem })).toHaveLength(2);
    })
});