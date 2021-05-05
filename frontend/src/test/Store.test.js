import {store} from '../store'

const business = {
  id: 1,
  name: "Haruka's Hamburgers",
};
const user = {
  id: 10,
  businessesAdministered: [
    business
  ]
};

describe("Setting actingAs", () => {
  test("Set a business", () => {
    store.actions.setAuthUser(user);
    store.actions.setActingAs(business);
    expect(store.getters.getActingAs()).toEqual(business);
  });

  test("Set a business, passing only ID", () => {
    store.actions.setAuthUser(user);
    store.actions.setActingAs(business.id);
    expect(store.getters.getActingAs()).toEqual(business);
  });

  test("Delete a business and switch back to individual", () => {
    store.actions.setAuthUser(user);
    store.actions.setActingAs(business);
    store.actions.deleteActingAs();
    expect(store.getters.getActingAs()).toEqual(null);
  });

  test("Set a business with bad ID", () => {
    store.actions.setAuthUser(user);
    expect(() => store.actions.setActingAs(4)).toThrow(Error);
  });
});