import {shallowMount} from "@vue/test-utils";
import ListingDetail from "@/views/business/ListingDetail";
import {globalStateMocks} from "#/testHelper";
import {Api} from "@/Api";
import {ApiRequestError} from "@/ApiRequestError";

jest.mock("@/Api");
let wrapper;

const listingId = 57;
const listings = {
  "data": {
    "results": [{
      "id": listingId,
      "inventoryItem": {
        "id": 101,
        "product": {
          "id": "WATT-420-BEANS",
          "name": "Watties Baked Beans - 420g can",
          "description": "Baked Beans as they should be.",
          "manufacturer": "Heinz Wattie's Limited",
          "recommendedRetailPrice": 2.2,
          "created": "2021-08-14T00:45:37.243Z",
          "images": [
            {
              "id": 1234,
              "filename": "/media/images/23987192387509-123908794328.png",
              "thumbnailFilename": "/media/images/23987192387509-123908794328_thumbnail.png"
            }
          ]
        },
        "quantity": 4,
        "pricePerItem": 6.5,
        "totalPrice": 21.99,
        "manufactured": "2021-08-14",
        "sellBy": "2021-08-14",
        "bestBefore": "2021-08-14",
        "expires": "2021-08-14",
        "business": {
          "id": 1,
          "name": "TestName",
          "address": {
            "streetNumber": "STREETNUMBER",
            "streetName": "STREETNAME",
            "suburb": "SUBURB",
            "postcode": "POSTCODE",
            "city": "CITY",
            "region": "REGION",
            "country": "COUNTRY"
          },
        }
      },
      "quantity": 3,
      "price": 17.99,
      "moreInfo": "Seller may be willing to consider near offers",
      "created": "2021-07-14T11:44:00Z",
      "closes": "2021-07-21T23:59:00Z",
    }]
  }
};



beforeEach(() => {
  jest.clearAllMocks();

  Api.getBusinessListings = jest.fn().mockResolvedValue(listings);
  wrapper = shallowMount(ListingDetail, {
    propsData: {
      businessId: 1,
      listingId: listingId
    },
    stubs: ['router-link'],
    mocks: {
      ...globalStateMocks()
    }
  });
});

afterEach(() => wrapper.destroy());


describe("Get listings API call", () => {
  test("Expect success", async () => {
    // Expect no error thrown
    await wrapper.vm.$nextTick();
    expect(Api.getBusinessListings.mock.calls.length).toBe(1);
  });

  test("API failure", async () => {
    // Expect no error thrown
    await wrapper.vm.$nextTick();
    Api.getBusinessListings = jest.fn().mockImplementation(() => Promise.reject(
        new ApiRequestError("ERR TEXT")
    ));
    await wrapper.vm.apiPipeline();
    await wrapper.vm.$nextTick();
    expect(Api.getBusinessListings.mock.calls.length).toBe(1);
    expect(wrapper.vm.apiErrorMessage).toBe("ERR TEXT");
  });


  test("listing not found, expect ApiRequestError", async () => {
    // Expect error thrown
    await expect(
        wrapper.vm.parseApiResponse(Promise.resolve({data: {results: []}}))
    ).rejects.toThrow(ApiRequestError);
  });

  // Currency/business information sent with listing information
  test("Currency parsed correctly", async () => {
    const mockCurrency = {
      "code": "FAB",
      "name": "Fabulous Fabian",
      "symbol": "F"
    };

    wrapper.vm.$helper.getCurrencyForBusinessByCountry = jest.fn().mockReturnValue(mockCurrency);
    await wrapper.vm.apiPipeline();
    // ApiPipeline called in lifecycle so can't mock calls before the method is called. Hence, need to run it twice
    await wrapper.vm.$nextTick();
    expect(wrapper.vm.$helper.getCurrencyForBusinessByCountry.mock.calls.length).toBe(1);
    expect(wrapper.vm.$helper.getCurrencyForBusinessByCountry.mock.calls[0][0]).toBe("COUNTRY");
    expect(wrapper.vm.currency).toEqual(mockCurrency);
  });
});

describe("buyButtonClicked", () => {
  test("Standard flow", async () => {
    Api.buyListing = jest.fn().mockResolvedValue(undefined);
    await wrapper.vm.buyButtonClicked();
    expect(wrapper.vm.listingWasPurchased).toBe(true);
  });

  test("Error cleared on success", async () => {
    wrapper.vm.buyApiErrorMessage = "SDF";
    Api.buyListing = jest.fn().mockResolvedValue(undefined);
    await wrapper.vm.buyButtonClicked();
    expect(wrapper.vm.buyApiErrorMessage).toBe(null);
  });

  test("Handles error gracefully", async () => {
    Api.buyListing = jest.fn().mockImplementation(() => Promise.reject(new ApiRequestError("ERR")));
    await wrapper.vm.buyButtonClicked();
    expect(wrapper.vm.buyApiErrorMessage).toBe("ERR");
  });

  test("Handles 401 error", async () => {
    Api.buyListing = jest.fn().mockImplementation(() => Promise.reject(new ApiRequestError("ERR")));
    Api.handle401 = jest.fn();
    await wrapper.vm.buyButtonClicked();
    expect(Api.handle401.mock.calls.length).toBe(1);
  });

  test("Clears loading indicator after success", async () => {
    Api.buyListing = jest.fn().mockResolvedValue(new Promise(resolve => setTimeout(resolve, 1000)));
    const promise = wrapper.vm.buyButtonClicked();
    expect(wrapper.vm.buyApiCallOngoing).toBe(true);
    await promise;
    expect(wrapper.vm.buyApiCallOngoing).toBe(false);
  });

  test("Clears loading indicator after failure", async () => {
    Api.buyListing = jest.fn().mockResolvedValue(new Promise((_, reject) => setTimeout(
        () => reject(new ApiRequestError("ERR")),
        1000)));
    const promise = wrapper.vm.buyButtonClicked();
    expect(wrapper.vm.buyApiCallOngoing).toBe(true);
    await promise;
    expect(wrapper.vm.buyApiCallOngoing).toBe(false);
  });
});
