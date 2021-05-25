import {shallowMount} from "@vue/test-utils";
import MarketplaceCard from "../../components/cards/MarketplaceCard";
import {globalStateMocks} from "../testHelper";

const cardTitle = '1982 Lada Samara';
const cardDesc = 'Beige, suitable for a hen house. Fair condition. Some rust. As is, where is. Will swap for budgerigar.';
const cardCreated = '2021-07-15T05:10:00Z';
const cardExpires = '2021-07-29T05:10:00Z';

const cardItem = {
  "id": 500,
  "creator": {
    "id": 100,
    "firstName": "John",
    "middleName": "Hector",
    "homeAddress": {
      "suburb": "Upper Riccarton",
      "city": "Christchurch"
    },
  },
  "section": "ForSale",
  "created": cardCreated,
  "displayPeriodEnd": cardExpires,
  "title": cardTitle,
  "description": cardDesc,
  "keywords": [
    {
      "id": 600,
      "name": "Vehicle",
      "created": "2021-07-15T05:10:00Z"
    }
  ]
};

describe("Test passing props to Marketplace card component", () => {
  test("Correct title and description show", () => {
    let wrapper = shallowMount(MarketplaceCard, {
      propsData: {
        card: cardItem
      },
      mocks: globalStateMocks(),
      stubs: ["router-link"]
    })
    expect(wrapper.props().card.title).toEqual(cardTitle);
    expect(wrapper.props().card.description).toEqual(cardDesc);
  })

  test("Calculates and displays correct date info", () => {
    let wrapper = shallowMount(MarketplaceCard, {
      propsData: {
        card: cardItem
      },
      mocks: globalStateMocks(),
      stubs: ["router-link"]
    });
    expect(wrapper.text()).toContain('Created ' + globalStateMocks().$helper.isoToDateString(cardCreated));
    expect(wrapper.text()).toContain('Expires ' + globalStateMocks().$helper.isoToDateString(cardExpires));
  })

  test("Receives the passed in keywords", () => {
    let wrapper = shallowMount(MarketplaceCard, {
      propsData: {
        card: cardItem
      },
      mocks: globalStateMocks(),
      stubs: ["router-link"]
    });
    expect(wrapper.props().card.keywords).toHaveLength(1);
  })
});
