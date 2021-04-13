import { EditDistance } from "./../EditDistance";

describe("Edit distance", () => {
  test("Equal", () => {
    expect(EditDistance.calculate("ABC", "ABC", 2, 3, 5)).toEqual(0);
  });

  test("Original empty", () => {
    expect(EditDistance.calculate("", "A", 2, 3, 5)).toEqual(2);
  });

  test("Modified empty", () => {
    expect(EditDistance.calculate("A", "", 2, 3, 5)).toEqual(3);
  });

  test("Both empty", () => {
    expect(EditDistance.calculate("", "", 2, 3, 5)).toEqual(0);
  });

  test("One insertion", () => {
    expect(EditDistance.calculate("ABD", "ABCD", 2, 3, 5)).toEqual(2);
  });

  test("One deletion", () => {
    expect(EditDistance.calculate("ABBC", "ABC", 2, 3, 5)).toEqual(3);
  });

  test("One substitution", () => {
    expect(EditDistance.calculate("ACC", "ABC", 2, 3, 5)).toEqual(5);
  });

  test("One substitution", () => {
    expect(EditDistance.calculate("ACC", "ABC", 2, 3, 5)).toEqual(5);
  });

  test("Insertion + deletion cheaper than substitution", () => {
    expect(EditDistance.calculate("ACC", "ABC", 2, 3, 10)).toEqual(5);
  });

  test("Insertion + deletion more expensive than substitution", () => {
    expect(EditDistance.calculate("ACC", "ABC", 2, 3, 4)).toEqual(4);
  });

  test("Insertion, deletion and substitution", () => {
    expect(EditDistance.calculate("ABBCDEGHIJKZMN", "ABCDEFGHIJKLMN", 7, 13, 17)).toEqual(7 + 13 + 17);
  });
});

