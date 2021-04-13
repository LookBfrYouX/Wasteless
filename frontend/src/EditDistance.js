// The **diff** algorithm. Transform source string $A = a_1\dots a_n$ into $B = b_1\dots b_m$ using the minimum number of insertions, deletions, and substitutions. This number is called the edit distance.
//
//     When a transformation is not needed, it is called the alignment operation, and has zero cost.
//
//     Work from end of string to start.
//
// - Copy/Alignment: If $a_i = b_j$, $D_{i,j} = D_{i-1, j-1}$
// - Otherwise, use the minimum of:
//     - Deletion: $D_{i,j} = D_{i-1,j} + 1$
// - Insertion: $D_{i,j} = D_{i, j-1} + 1$
// - Substitution: $D_{i,j} = D_{i-1, j-1} + 1$
// - Prefer substitution over deletion/insertion
// $$
// D_{i, j} = \begin{cases}
// 0 & i = j = 0 \\
// i & j = 0 \\
// j & i = 0 \\
// D_{i-1, j-1} & a_i = b_j \\
// 1 + \min(D_{i-1, j}, D_{i, j-1}, D_{i-1, j-1}) & \textrm{otherwise}
// \end{cases}
// $$a

export class EditDistance {
  original = "";
  modified = "";
  cache = {};
  insertCost = 1;
  deleteCost = 1;
  substituteCost = 1;
  constructor(original, modified, insertCost, deleteCost, substituteCost) {
    this.original = original;
    this.modified = modified;
    this.cache = {};
    this.insertCost = insertCost;
    this.deleteCost = deleteCost;
    this.substituteCost = substituteCost;
  }

  _getFromCache(i, j) {
    if (this.cache[i] !== undefined && this.cache[i][j] !== undefined) return this.cache[i][j];
  }

  _insertIntoCache(i, j, value) {
    if (this.cache[i] === undefined) this.cache[i] = {
      [j]: value
    };
    else this.cache[i][j] = value;
  }

  calculate(i = undefined, j = undefined) {
    if (i === undefined || j === undefined) {
      i = this.original.length - 1;
      j = this.modified.length - 1;
    }

    if (i <= 0) return j * this.insertCost;
    if (j <= 0) return i * this.deleteCost;
    
    const cached = this._getFromCache(i, j);
    if (cached !== undefined) return cached;
    
    
    if (this.original[i] === this.modified[j]) return this.calculate(i - 1, j - 1);
    //     - Deletion: $D_{i,j} = D_{i-1,j} + 1$
// - Insertion: $D_{i,j} = D_{i, j-1} + 1$
// - Substitution: $D_{i,j} = D_{i-1, j-1} + 1$
    const deletion = this.calculate(i - 1, j) + this.deleteCost;
    const insertion = this.calculate(i, j - 1) + this.insertCost;
    const substitution = this.calculate(i - 1, j - 1) + this.substituteCost;
    const result = Math.min(deletion, insertion, substitution);
    this._insertIntoCache(i, j, result);
    return result;
  }
}

// const original = "10 downing street";
// const modified = "1002 West Broad Street, Rosemont Terrace, Bethlehem, Lehigh County, Pennsylvania, 18018, United States".toLocaleLowerCase();
// console.log(original.length);
// console.log(modified.length);
// const instance = new EditDistance(original, modified, 1, 50, 50);
// console.log(instance.calculate());