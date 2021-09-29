package com.navbara_pigeons.wasteless.dao.Specifications;

import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dao.InventoryDao;
import com.navbara_pigeons.wasteless.dao.ListingDao;
import com.navbara_pigeons.wasteless.dao.ProductDao;
import com.navbara_pigeons.wasteless.dao.specifications.ListingSpecifications;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.enums.ListingSearchKeys;
import com.navbara_pigeons.wasteless.enums.NutriScore;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.model.ListingsSearchParams;
import com.navbara_pigeons.wasteless.service.InventoryService;
import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;

@SpringBootTest
class ListingSpecificationsTest extends MainTestProvider {

  ListingsSearchParams listingsSearchParams = new ListingsSearchParams();
  @Autowired
  ListingDao listingDao;
  @Autowired
  InventoryDao inventoryDao;
  @Autowired
  BusinessDao businessDao;
  @Autowired
  ProductDao productDao;

  @Autowired
  InventoryService inventoryService;

  @Test
  void resultsMeetSearchCriteriaTestPartialMatchingProductName() {
    List<ListingSearchKeys> searchKeys = new ArrayList<>();
    searchKeys.add(ListingSearchKeys.PRODUCT_NAME);
    listingsSearchParams.setSearchKeys(searchKeys);
    listingsSearchParams.setSearchParam("Sinker Milk");
    Specification<Listing> specification = ListingSpecifications
        .meetsSearchCriteria(listingsSearchParams);
    List<Listing> results = listingDao.findAll(specification);
    Assertions.assertEquals(5001, results.get(0).getId());
  }

  @Test
  void resultsMeetSearchCriteriaTestFullMatchingProductName() {
    List<ListingSearchKeys> searchKeys = new ArrayList<>();
    searchKeys.add(ListingSearchKeys.PRODUCT_NAME);
    listingsSearchParams.setSearchKeys(searchKeys);
    listingsSearchParams.setSearchParam("\"Sinker Milk Standard Blue Top\"");
    Specification<Listing> specification = ListingSpecifications
        .meetsSearchCriteria(listingsSearchParams);
    List<Listing> results = listingDao.findAll(specification);
    Assertions.assertEquals(5001, results.get(0).getId());
  }

  @Test
  @Transactional
  void resultsMeetSearchCriteriaTestFullMatchingAddress() throws Exception {
    List<ListingSearchKeys> searchKeys = new ArrayList<>();
    searchKeys.add(ListingSearchKeys.ADDRESS);
    listingsSearchParams.setSearchKeys(searchKeys);
    listingsSearchParams.setSearchParam("\"Christchurch\"");
    Specification<Listing> specification = ListingSpecifications
        .meetsSearchCriteria(listingsSearchParams);
    List<Listing> results = listingDao.findAll(specification);

    Listing expectedListing = listingDao.getListing(5001);
    Assertions.assertTrue(results.contains(expectedListing));
  }

  @Test
  void resultsMeetSearchCriteriaTestFilteredByPrice() {
    List<ListingSearchKeys> searchKeys = new ArrayList<>();
    searchKeys.add(ListingSearchKeys.PRODUCT_NAME);
    listingsSearchParams.setSearchKeys(searchKeys);
    listingsSearchParams.setSearchParam("");
    listingsSearchParams.setMinPrice(12.0);
    listingsSearchParams.setMaxPrice(12.0);
    Specification<Listing> specification = ListingSpecifications
        .meetsSearchCriteria(listingsSearchParams);
    List<Listing> results = listingDao.findAll(specification);
    Assertions.assertEquals(5002, results.get(0).getId());
  }

  @Test
  @Transactional
  void novaGroupFilter_withValidMinNovaGroup_expectFiltered() throws Exception {
    // Arrange
    List<ListingSearchKeys> searchKeys = new ArrayList<>();
    searchKeys.add(ListingSearchKeys.PRODUCT_NAME);
    listingsSearchParams.setSearchKeys(searchKeys);
    listingsSearchParams.setSearchParam("");
    listingsSearchParams.setMinNovaGroup(3);

    // Act
    Specification<Listing> specification = ListingSpecifications
        .meetsSearchCriteria(listingsSearchParams);
    List<Listing> results = listingDao.findAll(specification);

    Listing rated4Listing = listingDao.getListing(5001);  // This should be in the results
    Listing rated3Listing = listingDao.getListing(5004);  // This should be in the results
    Listing rated2Listing = listingDao.getListing(5005);  // This should NOT be in the results

    // Assert
    Assertions.assertTrue(results.contains(rated4Listing));
    Assertions.assertTrue(results.contains(rated3Listing));
    Assertions.assertFalse(results.contains(rated2Listing));
  }

  @Test
  @Transactional
  void novaGroupFilter_withValidMaxNovaGroup_expectFiltered() throws Exception {
    // Arrange
    List<ListingSearchKeys> searchKeys = new ArrayList<>();
    searchKeys.add(ListingSearchKeys.PRODUCT_NAME);
    listingsSearchParams.setSearchKeys(searchKeys);
    listingsSearchParams.setSearchParam("");
    listingsSearchParams.setMaxNovaGroup(3);

    // Act
    Specification<Listing> specification = ListingSpecifications
        .meetsSearchCriteria(listingsSearchParams);
    List<Listing> results = listingDao.findAll(specification);

    Listing rated4Listing = listingDao.getListing(5003);  // This should NOT be in the results
    Listing rated3Listing = listingDao.getListing(5001);  // This should be in the results
    Listing rated2Listing = listingDao.getListing(5006);  // This should be in the results

    // Assert
    Assertions.assertFalse(results.contains(rated4Listing));
    Assertions.assertTrue(results.contains(rated3Listing));
    Assertions.assertTrue(results.contains(rated2Listing));
  }

  @Test
  @Transactional
  void novaGroupFilter_withValidRangeNovaGroup_expectFiltered() throws Exception {
    // Arrange
    List<ListingSearchKeys> searchKeys = new ArrayList<>();
    searchKeys.add(ListingSearchKeys.PRODUCT_NAME);
    listingsSearchParams.setSearchKeys(searchKeys);
    listingsSearchParams.setSearchParam("");
    listingsSearchParams.setMinNovaGroup(2);
    listingsSearchParams.setMaxNovaGroup(3);

    // Act
    Specification<Listing> specification = ListingSpecifications
        .meetsSearchCriteria(listingsSearchParams);
    List<Listing> results = listingDao.findAll(specification);

    Listing rated4Listing = listingDao.getListing(5003);  // This should NOT be in the results
    Listing rated3Listing = listingDao.getListing(5001);  // This should be in the results
    Listing rated2Listing = listingDao.getListing(5006);  // This should be in the results
    Listing rated1Listing = listingDao.getListing(5005);  // This should NOT be in the results

    // Assert
    Assertions.assertFalse(results.contains(rated4Listing));
    Assertions.assertTrue(results.contains(rated3Listing));
    Assertions.assertTrue(results.contains(rated2Listing));
    Assertions.assertFalse(results.contains(rated1Listing));
  }

  @Test
  @Transactional
  void resultsMeetSearchCriteriaTestFilteredByNutriScore() throws Exception {
    // Arrange
    List<ListingSearchKeys> searchKeys = new ArrayList<>();
    searchKeys.add(ListingSearchKeys.PRODUCT_NAME);
    listingsSearchParams.setSearchKeys(searchKeys);
    listingsSearchParams.setSearchParam("");
    listingsSearchParams.setMinNutriScore(NutriScore.A);
    listingsSearchParams.setMaxNutriScore(NutriScore.B);

    // Act
    Specification<Listing> specification = ListingSpecifications
        .meetsSearchCriteria(listingsSearchParams);
    List<Listing> results = listingDao.findAll(specification);

    Listing aRatedListing = listingDao.getListing(5004);  // This should be in the results
    Listing eRatedListing = listingDao.getListing(5008);  // This should NOT be in the results

    // Assert
    Assertions.assertTrue(results.contains(aRatedListing));
    Assertions.assertFalse(results.contains(eRatedListing));
  }
}
