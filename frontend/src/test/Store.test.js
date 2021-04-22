import { store } from '../store'

const business = {
  id: 1,
  name: "Haruka's Hamburgers",
};
const user = {
  id: 10,
  businesses: [
    business
  ]
};

describe("Setting actingAs", () => {
  test("Set a business", () => {
    store.actions.setAuthUser(user);
    store.actions.setActingAs(business);
    expect(store.getters.getActingAs()).toEqual(business);
  });

  test("Delete a business and switch back to individual", () => {
    store.actions.setAuthUser(user);
    store.actions.setActingAs(business);
    store.actions.deleteActingAs();
    expect(store.getters.getActingAs()).toEqual(null);
  })
})