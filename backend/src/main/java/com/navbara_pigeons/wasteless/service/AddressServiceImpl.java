package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.AddressDao;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.exception.AddressValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressServiceImpl implements AddressService {

  private final CountryDataFetcherService countryDataFetcherService;
  private final AddressDao addressDao;

  @Autowired
  public AddressServiceImpl(CountryDataFetcherService countryDataFetcherService,
      AddressDao addressDao) {
    this.countryDataFetcherService = countryDataFetcherService;
    this.addressDao = addressDao;
  }

  /**
   * Saves an address TODO add tests
   *
   * @param address address to save
   * @throws AddressValidationException if required fields are missing or empty, or that the country
   *                                    is unknown
   */
  @Override
  @Transactional
  public void saveAddress(Address address) throws AddressValidationException {
    if (!isCountryValid(address.getCountry())) {
      throw new AddressValidationException(
          "Country does not exist or is not known: " + address.getCountry());
    }

    addressDao.saveAddress(address);
  }

  /**
   * Checks if the country is valid
   *
   * @param country name of country
   * @return true if country known
   */
  private boolean isCountryValid(String country) {
    return this.countryDataFetcherService.countryExists(country);
  }
}
