import {shallowMount} from "@vue/test-utils";
import CreateListing from "@/views/business/ListingCreate";
import {globalStateMocks} from "#/testHelper";
import {ApiRequestError} from "@/ApiRequestError";
import {Api} from "@/Api";

jest.mock("@/Api");

let wrapper;

const mockInventory =
    [{
      "id": 10,
      "product": {
        "id": "1",
        "name": "Earl Grey",
      },
      "quantity": 4,
      "pricePerItem": 7.0,
      "totalPrice": 25.00,
      "expires": "2021-08-03"
    },
      {
        "id": 20,
        "product": {
          "id": "2",
          "name": "English Breakfast",
        },
        "quantity": 10,
        "pricePerItem": null,
        "totalPrice": 40.00,
        "expires": "2021-09-02"
      },
      {
        "id": 30,
        "product": {
          "id": "2",
          "name": "English Breakfast",
        },
        "quantity": 7,
        "pricePerItem": 10.0,
        "totalPrice": null,
        "expires": "2021-10-01"
      },
      {
        "id": 40,
        "product": {
          "id": "3",
          "name": "Green tea",
        },
        "quantity": 9,
        "pricePerItem": 6.0,
        "totalPrice": 50.00,
        "expires": "2021-10-01"
      },
    ];

const mockListings =
    [{
      "id": 100,
      "inventoryItem": {
        "id": 10,
        "product": {
          "id": "1",
          "name": "Earl Grey",
        },
        "quantity": 4,
        "pricePerItem": 7.0,
        "totalPrice": 25.00,
        "expires": "2021-05-22"
      },
      "quantity": 3,
    },
      {
        "id": 200,
        "inventoryItem": {
          "id": 10,
          "product": {
            "id": "1",
            "name": "Earl Grey",
          },
          "quantity": 4,
          "pricePerItem": 7.0,
          "totalPrice": 25.00,
          "expires": "2021-05-22"
        },
        "quantity": 1,
      },
      {
        "id": 300,
        "inventoryItem": {
          "id": 20,
          "product": {
            "id": "3",
            "name": "English Breakfast",
          },
          "quantity": 10,
          "pricePerItem": 5.0,
          "totalPrice": 40.00,
          "expires": "2021-09-22"
        },
        "quantity": 4,
      },
    ];

beforeEach(() => {
  wrapper = shallowMount(CreateListing, {
    propsData: {
      businessId: 1
    },
    mocks: {
      ...globalStateMocks(),
    }
  });
});

afterEach(() => wrapper.destroy());

describe("getInventory method", () => {
  test("Successfully got inventory", async () => {
    let mockInventoryResponse = {
      data: mockInventory
    };
    Api.getBusinessInventory.mockResolvedValue(mockInventoryResponse);
    let isSuccessfulResponse = await wrapper.vm.getInventory();
    expect(isSuccessfulResponse).toEqual(true);
  });

  test("Get inventory failed", async () => {
    Api.getBusinessInventory.mockImplementation(
        () => Promise.reject(new ApiRequestError("Request Failed")));
    let isSuccessfulResponse = await wrapper.vm.getInventory();
    expect(isSuccessfulResponse).toEqual(false);
  });
});

describe("getListings method", () => {
  test("Successfully got listings", async () => {
    let mockListingsResponse = {
      data: mockListings
    };
    Api.getBusinessListings.mockResolvedValue(mockListingsResponse);
    let response = await wrapper.vm.getListings();
    expect(response).toEqual(mockListings);
  });

  test("Get listings failed", async () => {
    Api.getBusinessListings.mockImplementation(
        () => Promise.reject(new ApiRequestError("Request Failed")));
    let response = await wrapper.vm.getListings();
    expect(response).toEqual(undefined);
  });
});

describe("getAvailableInventoryItem method", () => {
  test("Inventory item not listed yet", async () => {
    wrapper.setData({
      inventory: mockInventory
    });
    let mockListingsResponse = {
      data: mockListings
    };
    Api.getBusinessListings.mockResolvedValue(mockListingsResponse);
    await wrapper.vm.getAvailableInventoryItem();
    expect(wrapper.vm.inventory[2].quantityRemaining).toEqual(
        wrapper.vm.inventory[2].quantity);
  });

  test("Inventory with already one listing exits", async () => {
    wrapper.setData({
      inventory: mockInventory
    });
    let mockListingsResponse = {
      data: mockListings
    };
    Api.getBusinessListings.mockResolvedValue(mockListingsResponse);
    await wrapper.vm.getAvailableInventoryItem();
    expect(wrapper.vm.inventory[1].quantityRemaining).toEqual(6);
  });

  test("No more item available to list from inventory", async () => {
    wrapper.setData({
      inventory: mockInventory
    });
    let mockListingsResponse = {
      data: mockListings
    };
    Api.getBusinessListings.mockResolvedValue(mockListingsResponse);
    await wrapper.vm.getAvailableInventoryItem();
    expect(wrapper.vm.inventory[0].quantityRemaining).toEqual(0);
  });
});

describe("getProducts computed", () => {
  test("Inventory is not ready yet", () => {
    wrapper.setData({
      inventory: null
    });
    let products = wrapper.vm.getProducts;
    expect(products).toEqual([]);
  });

  test("Successfully got products from inventory", () => {
    wrapper.setData({
      inventory: mockInventory
    });
    let products = wrapper.vm.getProducts;
    expect(products.length).toEqual(3);
  });
});

describe("filteredInventory computed", () => {
  test("Inventory is not ready yet", () => {
    wrapper.setData({
      inventory: null
    });
    let filteredInventoryArray = wrapper.vm.filteredInventory;
    expect(filteredInventoryArray).toEqual([]);
  });

  test("Product not selected yet", () => {
    wrapper.setData({
      selectedProductId: null
    });
    let filteredInventoryArray = wrapper.vm.filteredInventory;
    expect(filteredInventoryArray).toEqual([]);
  });

  test("Inventory array successfully filtered", () => {
    wrapper.setData({
      inventory: mockInventory,
      selectedProductId: "2"
    });
    let filteredInventoryArray = wrapper.vm.filteredInventory;
    expect(filteredInventoryArray.length).toEqual(2);
  });
});

describe("defaultPrice computed", () => {
  test("User haven't selected inventory yet", () => {
    wrapper.setData({
      selectedInventoryItem: null,
    });
    let result = wrapper.vm.defaultPrice;
    expect(result).toEqual("0.00");
  });

  test("User specified listing quantity less than or equal to zero", () => {
    wrapper.setData({
      selectedInventoryItem: mockInventory[0],
      quantity: 0
    });
    let result = wrapper.vm.defaultPrice;
    expect(result).toEqual("0.00");
  });

  test("User specified quantity more than maximum quantity", async () => {
    let mockInventoryItem = mockInventory[0];
    let quantityExceeded = mockInventoryItem.quantity + 1;
    wrapper.setData({
      selectedInventoryItem: mockInventoryItem,
      quantity: quantityExceeded
    });
    await wrapper.vm.$nextTick();
    let result = wrapper.vm.defaultPrice;
    expect(result).toEqual("0.00");
  });

  test("Total price of inventory item is null", () => {
    let mockInventoryItem = mockInventory[2];
    let quantityFullSize = mockInventoryItem.quantity;
    wrapper.setData({
      selectedInventoryItem: mockInventoryItem,
      quantity: quantityFullSize,
    });
    let result = wrapper.vm.defaultPrice;
    expect(result).toEqual("0.00");
  });

  test("Price per item of inventory item is null", () => {
    let mockInventoryItem = mockInventory[1];
    let quantityFullSize = mockInventoryItem.quantity;
    wrapper.setData({
      selectedInventoryItem: mockInventoryItem,
      quantity: quantityFullSize - 1,
    });
    let result = wrapper.vm.defaultPrice;
    expect(result).toEqual("0.00");
  })

  test("Listing full quantity in the inventory", async () => {
    let mockInventoryItem = mockInventory[3];
    mockInventoryItem.quantityRemaining = 9;
    let quantityFullSize = mockInventoryItem.quantity;
    wrapper.setData({
      selectedInventoryItem: mockInventoryItem,
      quantity: quantityFullSize,
    });
    await wrapper.vm.$nextTick();
    let result = wrapper.vm.defaultPrice;
    let expected = (mockInventoryItem.totalPrice).toFixed(2);
    expect(result).toEqual(expected);
  });

  test("Listing partial quantity in the inventory", async () => {
    let mockInventoryItem = mockInventory[1];
    mockInventoryItem.quantityRemaining = 6;
    let quantityFullSize = mockInventoryItem.quantity;
    let quantityPartial = quantityFullSize - 1;
    wrapper.setData({
      selectedInventoryItem: mockInventoryItem,
      quantity: quantityPartial,
    });
    await wrapper.vm.$nextTick();
    let result = wrapper.vm.defaultPrice;
    let expected = (mockInventoryItem.pricePerItem * quantityPartial).toFixed(
        2);
    expect(result).toEqual(expected);
  });
});

describe("addListing method", () => {
  test("API called", async () => {
    let successfulResponse = {
      data: {
        "inventoryItemId": 10
      }
    }
    wrapper.setData({
      closeDate: null,
      closeTime: null,
      selectedInventoryItem: mockInventory[0],
      quantity: "2",
      moreInfo: ""
    });
    Api.addBusinessListings.mockResolvedValue(successfulResponse);
    await wrapper.vm.addListing();
    expect(Api.addBusinessListings.mock.calls.length).toBe(1);
  });
});