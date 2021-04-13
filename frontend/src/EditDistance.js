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
   * @param {*} original original string
   * @param {*} modified modified string to compare to the original
   * @param {*} insertCost cost of an insertion operation
   * @param {*} deleteCost cost of an deletion operation
   * @param {*} substituteCost cost of an substitution operation
   */
  constructor(original, modified, insertCost = 1, deleteCost = 1, substituteCost = 1) {
    this.original = original;
    this.modified = modified;
    this.cache = {};
    this.insertCost = insertCost;
    this.deleteCost = deleteCost;
    this.substituteCost = substituteCost;
  }

  /**
   * Gets edit distance from the cache
   * @param {*} i index in original string
   * @param {*} j index in modified string
   * @returns undefined if not present, edit distance otherwise
   */
  _getFromCache(i, j) {
    if (this.cache[i] !== undefined && this.cache[i][j] !== undefined) return this.cache[i][j];
  }

  /**
   * Inserts edit distance into cache
   * @param {*} i index in original string
   * @param {*} j index in modified string
   * @param {*} value 
   */
  _insertIntoCache(i, j, value) {
    if (this.cache[i] === undefined) this.cache[i] = {
      [j]: value
    };
    else this.cache[i][j] = value;
  }

  /**
   * Calculates edit distance
   * @param {*} i index in original string; edit distance if the original string was a substring from index 0 to index i inclusive
   * @param {*} j index in modified string; edit distance if the modified string was a substring from index 0 to index j inclusive
   * @returns edit distance
   */
  _calculate(i, j) {
    if (i <= 0) return j * this.insertCost;
    if (j <= 0) return i * this.deleteCost;
    
    const cached = this._getFromCache(i, j);
    if (cached !== undefined) return cached;
    
    
    if (this.original[i] === this.modified[j]) return this.calculate(i - 1, j - 1);
    
    const deletion = this.calculate(i - 1, j) + this.deleteCost;
    const insertion = this.calculate(i, j - 1) + this.insertCost;
    const substitution = this.calculate(i - 1, j - 1) + this.substituteCost;
    
    const result = Math.min(deletion, insertion, substitution);
    this._insertIntoCache(i, j, result);
    return result;
  }

  /**
   * Calculates edit distance between original and modified strings
   * @returns edit distance
   */
  calculate() {
    const i = this.original.length - 1;
    const j = this.modified.length - 1;
    return this._calculate(i, j);
  }
}