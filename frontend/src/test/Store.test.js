import { store } from '../store'

describe("Setting actingAs", () => {
  test("Set a business", () => {
    const business = {
      id: 1,
      name: "Haruka's Hamburgers",
    }
    store.actions.setActingAs(business);
    expect(store.getters.getActingAs().name).toEqual(business.name);
  });

  test("Delete a business and switch back to individual", () => {
    const business = {
      id: 1,
      name: "Haruka's Hamburgers",
    }
    store.actions.setActingAs(business);
    store.actions.deleteActingAs();
    expect(store.getters.getActingAs()).toEqual(null);
  })
})