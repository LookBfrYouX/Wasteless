package com.navbara_pigeons.wasteless.service;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.navbara_pigeons.wasteless.dao.AddressDao;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.exception.AddressValidationException;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class AddressServiceImplTest extends ServiceTestProvider {

  @Mock
  CountryDataFetcherService countryDataFetcherService;
  @Mock
  AddressDao addressDao;
  @InjectMocks
  AddressServiceImpl addressService;

  @Test
  void saveAddress_normal() {
    Address address = makeAddress();
    when(countryDataFetcherService.countryExists(address.getCountry())).thenReturn(true);
    doNothing().when(addressDao).saveAddress(address);
    Assertions.assertDoesNotThrow(() -> addressService.saveAddress(address));
  }

  @Test
  void saveAddress_badCountry() {
    Address address = makeAddress();
    when(countryDataFetcherService.countryExists(address.getCountry())).thenReturn(false);
    assertThrows(AddressValidationException.class, () -> addressService.saveAddress(address));
  }

  @Test
  void saveAddress_countryNull() {
    Address address = makeAddress();
    when(countryDataFetcherService.countryExists(address.getCountry())).thenReturn(false);
    assertThrows(AddressValidationException.class, () -> addressService.saveAddress(address));
  }
}
