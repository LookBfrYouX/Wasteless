/**
 * Helper methods that may be used by multiple pages. It is added to the vue prototype so
 * the component does not need to explicitly import this class
 */
export const helper = {
  /**
   * Given address object, convert it to string, stripping out undefined components
   * @param {object} address 
   * @returns 
   */
  addressToString(address) {
    const numberUndef = address.streetNumber == undefined || address.streetNumber.trim().length == 0;
    const nameUndef = address.streetName == undefined || address.streetName.trim().length == 0;

    let street = undefined;
    if (numberUndef && !nameUndef) street = address.streetNumber;
    // if street number defined but street name not, don't show either
    else if (!numberUndef && !nameUndef) street = `${address.streetNumber} ${address.streetName}`;
    
    return [
      street,
      address.city,
      address.region,
      address.postcode,
      address.country
    ].filter(component => typeof component == "string" && component.trim().length > 0).join(', ');
  }
}
