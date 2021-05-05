package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.exception.AddressValidationException;

public interface AddressService {

  public void saveAddress(Address address) throws AddressValidationException;
}
