package com.navbara_pigeons.wasteless.service;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import com.navbara_pigeons.wasteless.dao.AddressDao;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.exception.AddressValidationException;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class AddressServiceImplTest extends ServiceTestProvider {

  @Mock
  CountryDataFetcherService countryDataFetcherService;

  @Mock
  AddressDao mockAddressDao;

  @InjectMocks
  AddressServiceImpl addressService;

  @Test
  void saveAddress_normal() throws AddressValidationException {
    Address address = makeAddress();
    when(countryDataFetcherService.countryExists(address.getCountry())).thenReturn(true);
    addressService.saveAddress(address);
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
