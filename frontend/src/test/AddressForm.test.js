import {shallowMount} from "@vue/test-utils";
import AddressForm from "../components/AddressForm";

let wrapper;

jest.useFakeTimers();

beforeEach(() => {
  wrapper = shallowMount(AddressForm, {
    propsData: {
      address: {
        streetNumber: "",
        streetName: "",
        city: "",
        region: "",
        postcode: "",
        country: "",
      }
    },
    mocks: {},
    stubs: {},
    methods: {}
  });
});

afterEach(() => wrapper.destroy());

const standardAddress = () => {
  return {
    streetNumber: "NUM",
    streetName: "STREET",
    city: "C",
    region: "REGION",
    postcode: "P",
    country: "COUNTRY",
  };
}

const standardOSMAddress = () => {
  return {
    housenumber: "NUM", // streetNumber
    street: "STREET", // streetName
    county: "C", // city
    state: "REGION", // region
    postcode: "P",
    country: "COUNTRY",
    // osm_id,
  };
}

describe("text entry", () => {
  test("props updated", async () => {
    wrapper.vm.addressSuggestionsPipeline = jest.fn();

    await wrapper.setProps({
      address: standardAddress()
    });

    jest.runAllTimers();

    expect(wrapper.vm.addressSuggestionsPipeline.mock.calls.length).toBe(1);
  });

  test("suggestionSelected", async () => {
    wrapper.vm.activeAddressInputName = "city";
    wrapper.vm.suggestionSelected(standardAddress());

    await wrapper.vm.$nextTick();

    const events = wrapper.emitted("addressupdate");
    expect(events.length).toBe(1);
    const {toString, ...address} = events[0][0];

    let expected = standardAddress();
    expected.streetNumber = expected.streetName = "";
    // Should only return city and up

    expect(address).toEqual(expected);
    expect(toString()).toEqual(
        wrapper.vm.generateAddressStringFromAddressComponents(expected,
            "city"));
  });

  test("actually updates on address input", async () => {
    wrapper.vm.addressSuggestionsPipeline = jest.fn();

    const postcode = "POSTCODE";
    wrapper.setData({
      activeAddressInputName: "postcode"
    });

    wrapper.vm.onAddressInput(postcode);

    await wrapper.vm.$nextTick();

    const events = wrapper.emitted("addressupdate");
    expect(events.length).toBe(1);
    const {toString, ...address} = events[0][0];

    expect(address).toEqual({
      streetNumber: "",
      streetName: "",
      postcode: postcode,
      city: "",
      region: "",
      country: "",
    });

    expect(toString()).toEqual(postcode);

    jest.runAllTimers();

    expect(wrapper.vm.addressSuggestionsPipeline.mock.calls.length).toBe(1);
  });
});

test("generateAddressSuggestions", () => {
  let a = standardOSMAddress();
  let b = standardOSMAddress();
  b.housenumber = "DIFF";
  let c = standardOSMAddress();
  c.country = "DIFF COUNTRY";

  const name = "postcode";
  wrapper.setData({
    addressSuggestionsRaw: [a, b, c].map(addr => {
      return {
        type: "Feature",
        properties: addr
      }
    }),
    activeAddressInputName: name
  });

  expect(wrapper.vm.generateAddressSuggestions().map(addr => {
    delete addr.toString; // already tested elsewhere
    return addr;
  })).toEqual(
      // a same as b in terms of the string value, so only a or b should be given
      [b, c].map(addr => {
        return wrapper.vm.mapOSMPropertiesToAddressComponents(addr);
      })
  );
});

describe("mapOSMPropertiesToAddressComponents", () => {
  test("Standard", () => {
    expect(wrapper.vm.mapOSMPropertiesToAddressComponents(standardOSMAddress()))
    .toEqual(standardAddress())
  });

  test("Missing field", () => {
    let addressOSM = standardOSMAddress();
    delete addressOSM.county;
    let address = standardAddress();
    delete address.city;

    expect(wrapper.vm.mapOSMPropertiesToAddressComponents(addressOSM)).toEqual(
        address);
  });

  test("Street number missing", () => {
    let addressOSM = standardOSMAddress();
    delete addressOSM.housenumber;
    let address = standardAddress();
    delete address.streetNumber;

    expect(wrapper.vm.mapOSMPropertiesToAddressComponents(addressOSM)).toEqual(
        address);
  });

  test("Street name missing", () => {
    let addressOSM = standardOSMAddress();
    delete addressOSM.street;
    let address = standardAddress();
    delete address.streetName;

    expect(wrapper.vm.mapOSMPropertiesToAddressComponents(addressOSM)).toEqual(
        address);
  });
});

describe("generateAddressStringFromAddressComponents", () => {
  test("All sections", () => {
    expect(wrapper.vm.generateAddressStringFromAddressComponents(
        standardAddress()
    )).toEqual("NUM STREET, C, REGION, P, COUNTRY");
  });

  test("street number blank", () => {
    expect(wrapper.vm.generateAddressStringFromAddressComponents(
        {
          ...standardAddress(),
          streetNumber: ""
        }
    )).toEqual("STREET, C, REGION, P, COUNTRY");
  });

  test("street name blank", () => {
    expect(wrapper.vm.generateAddressStringFromAddressComponents(
        {
          ...standardAddress(),
          streetName: ""
        }
    )).toEqual("C, REGION, P, COUNTRY");
  });

  test("Blank section", () => {
    let address = standardAddress();
    address.streetName = "";
    expect(wrapper.vm.generateAddressStringFromAddressComponents(
        address,
    )).toEqual("C, REGION, P, COUNTRY");
  });

  test("Postcode and up", () => {
    expect(wrapper.vm.generateAddressStringFromAddressComponents(
        standardAddress(),
        "postcode"
    )).toEqual("P, COUNTRY");
  });

  test("Invalid section name", () => {
    expect(wrapper.vm.generateAddressStringFromAddressComponents(
        standardAddress(),
        "INVALID"
    )).toEqual("NUM STREET, C, REGION, P, COUNTRY");
  });

  test("Invalid section name + one undefined", () => {
    let address = standardAddress();
    expect(wrapper.vm.generateAddressStringFromAddressComponents(
        address,
        "INVALID"
    )).toEqual("NUM STREET, C, REGION, P, COUNTRY");
  });

  test("Invalid section name + one undefined + failOnErr", () => {
    let address = standardAddress();
    delete address.streetName;
    expect(wrapper.vm.generateAddressStringFromAddressComponents(
        address,
        "INVALID",
        true
    )).toEqual(undefined);
  });
});
