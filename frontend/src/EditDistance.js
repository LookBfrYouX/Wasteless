/**
 * Calculates edit distance between two strings - Levenshtein distance
 * Weights for insertion/deletion/substitution cost can be customized
 */
export class EditDistance {
  original = "";
  modified = "";
  cache = {};
  insertCost = 1;
  deleteCost = 1;
  substituteCost = 1;

  /**
   * Creates EditDistance instance
   * @param {string} original original string
   * @param {string} modified modified string to compare to the original
   * @param {number} insertCost cost of an insertion operation
   * @param {number} deleteCost cost of an deletion operation
   * @param {number} substituteCost cost of an substitution operation
   */
  constructor(original, modified, insertCost = 1, deleteCost = 1,
      substituteCost = 1) {
    this.original = original;
    this.modified = modified;
    this.cache = {};
    this.insertCost = insertCost;
    this.deleteCost = deleteCost;
    this.substituteCost = substituteCost;
  }

  /**
   * Static method which calculates edit distance between original and modified strings
   * @param {string} original original string
   * @param {string} modified modified string to compare to the original
   * @param {number} insertCost cost of an insertion operation
   * @param {number} deleteCost cost of an deletion operation
   * @param {number} substituteCost cost of an substitution operation
   */
  static calculate(original, modified, insertCost = 1, deleteCost = 1,
      substituteCost = 1) {
    return (new EditDistance(original, modified, insertCost, deleteCost,
        substituteCost)).calculate();
  }

  /**
   * Gets edit distance from the cache
   * @param {number} i index in original string
   * @param {number} j index in modified string
   * @returns undefined if not present, edit distance otherwise
   */
  _getFromCache(i, j) {
    if (this.cache[i] !== undefined && this.cache[i][j]
        !== undefined) {
      return this.cache[i][j];
    }
  }

  /**
   * Inserts edit distance into cache
   * @param {number} i index in original string
   * @param {number} j index in modified string
   * @param {number} value
   */
  _insertIntoCache(i, j, value) {
    if (this.cache[i] === undefined) {
      this.cache[i] = {
        [j]: value
      };
    } else {
      this.cache[i][j] = value;
    }
  }

  /**
   * Calculates edit distance
   * @param {number | undefined} i index in original string; edit distance if the original string was a substring from index 0 to index i inclusive
   * @param {number | undefined} j index in modified string; edit distance if the modified string was a substring from index 0 to index j inclusive
   * @returns edit distance
   */
  _calculate(i = undefined, j = undefined) {
    if (i == undefined || j == undefined) {
      i = this.original.length - 1;
      j = this.modified.length - 1;
    }
    if (i < 0 && j < 0) {
      return 0;
    }// 0 means string length 1 so need to insert/delete length of other string + 1
    else if (i < 0) {
      return (j + 1) * this.insertCost;
    } else if (j < 0) {
      return (i + 1) * this.deleteCost;
    }

    const cached = this._getFromCache(i, j);
    if (cached !== undefined) {
      return cached;
    }

    if (this.original[i] === this.modified[j]) {
      const distance = this._calculate(i - 1, j - 1);
      this._insertIntoCache(i, j, distance);
      return distance;
    }

    const deletion = this._calculate(i - 1, j) + this.deleteCost;
    const insertion = this._calculate(i, j - 1) + this.insertCost;
    const substitution = this._calculate(i - 1, j - 1) + this.substituteCost;

    const result = Math.min(deletion, insertion, substitution);
    this._insertIntoCache(i, j, result);
    return result;
  }

  /**
   * Calculates edit distance between original and modified strings
   * @returns edit distance
   */
  calculate() {
    return this._calculate();
  }
}
