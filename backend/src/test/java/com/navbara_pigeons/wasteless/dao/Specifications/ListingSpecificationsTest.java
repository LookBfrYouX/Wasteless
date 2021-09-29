package com.navbara_pigeons.wasteless.dao.Specifications;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dao.InventoryDao;
import com.navbara_pigeons.wasteless.dao.ListingDao;
import com.navbara_pigeons.wasteless.dao.ProductDao;
import com.navbara_pigeons.wasteless.dao.specifications.ListingSpecifications;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.enums.ListingSearchKeys;
import com.navbara_pigeons.wasteless.enums.NutriScore;
import com.navbara_pigeons.wasteless.enums.NutritionFactsLevel;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.model.ListingsSearchParams;
import com.navbara_pigeons.wasteless.service.InventoryService;
import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import org.junit.Assert;
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
    assertTrue(results.stream().map(el -> el.getId()).collect(Collectors.toSet()).contains(5001l));
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
    assertTrue(results.stream().map(el -> el.getId()).collect(Collectors.toSet()).contains(5001l));
  }

  @Test
  void resultsMeetSearchCriteriaTestFullMatchingAddress() throws ListingValidationException {
    List<ListingSearchKeys> searchKeys = new ArrayList<>();
    searchKeys.add(ListingSearchKeys.ADDRESS);
    listingsSearchParams.setSearchKeys(searchKeys);
    listingsSearchParams.setSearchParam("\"Christchurch\"");
    Specification<Listing> specification = ListingSpecifications
        .meetsSearchCriteria(listingsSearchParams);
    List<Listing> results = listingDao.findAll(specification);
    assertTrue(results.stream().map(el -> el.getId()).collect(Collectors.toSet()).contains(5001l));
  }

  @Test
  void resultsMeetSearchCriteriaTestFilteredByPrice() throws ListingValidationException {
    List<ListingSearchKeys> searchKeys = new ArrayList<>();
    searchKeys.add(ListingSearchKeys.PRODUCT_NAME);
    listingsSearchParams.setSearchKeys(searchKeys);
    listingsSearchParams.setSearchParam("");
    listingsSearchParams.setMinPrice(12.0);
    listingsSearchParams.setMaxPrice(12.0);
    Specification<Listing> specification = ListingSpecifications
        .meetsSearchCriteria(listingsSearchParams);
    List<Listing> results = listingDao.findAll(specification);
    assertTrue(results.stream().map(el -> el.getId()).collect(Collectors.toSet()).contains(5002l));
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

    Listing rated4Listing = listingDao.getListing(5001);  // This should NOT be in the results
    Listing rated3Listing = listingDao.getListing(5004);  // This should be in the results
    Listing rated2Listing = listingDao.getListing(5005);  // This should be in the results

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

    Listing rated4Listing = listingDao.getListing(5001);  // This should NOT be in the results
    Listing rated3Listing = listingDao.getListing(5004);  // This should be in the results
    Listing rated2Listing = listingDao.getListing(5005);  // This should be in the results
    Listing rated1Listing = listingDao.getListing(5007);  // This should NOT be in the results

    // Assert
    Assertions.assertFalse(results.contains(rated4Listing));
    Assertions.assertTrue(results.contains(rated3Listing));
    Assertions.assertTrue(results.contains(rated2Listing));
    Assertions.assertFalse(results.contains(rated1Listing));
  }

  /**
   * Test that the addNutritionLevelPredicate works. Can't figure out to check if the Predicate
   * objects it creates are the right one and still need to test it being integrated with the
   * filtering method, so this tests both at the same time. Couldn't figure out how to test the
   * meetsSearchCriteria method without calling the database unfortunately
   * <p>
   * Recommend using the below query to figure out what the correct response should be: SELECT
   * listing.ID, product.ID AS PRODUCT_ID, product.FAT, product.SATURATED_FAT, product.SUGARS,
   * product.SALT FROM listing JOIN inventory_item ON listing.INVENTORY_ITEM_ID = inventory_item.ID
   * JOIN product ON inventory_item.PRODUCT_ID = product.ID
   * <p>
   * ORDER BY listing.ID
   */
  @Test
  void resultsMeetSearchCriteria_filterByNutrientLevelsFatMultipleLevels_expectFiltered() {
    listingsSearchParams.setFat(List.of(NutritionFactsLevel.MODERATE, NutritionFactsLevel.HIGH));
    Specification<Listing> specification = ListingSpecifications
        .meetsSearchCriteria(listingsSearchParams);
    List<Listing> results = listingDao.findAll(specification);
    assertEquals(2, results.size());
    assertEquals(List.of(5006, 5007).toString(),
        results.stream().map(el -> el.getId()).sorted().collect(Collectors.toList()).toString());
  }


  @Test
  void resultsMeetSearchCriteria_filterByNutrientLevelsSaturatedFatOneLevel_expectFiltered() {
    listingsSearchParams.setSaturatedFat(List.of(NutritionFactsLevel.LOW));
    Specification<Listing> specification = ListingSpecifications
        .meetsSearchCriteria(listingsSearchParams);
    List<Listing> results = listingDao.findAll(specification);
    assertEquals(2, results.size());
    assertEquals(List.of(5003, 5005).toString(),
        results.stream().map(el -> el.getId()).sorted().collect(Collectors.toList()).toString());
  }


  @Test
  void resultsMeetSearchCriteria_filterByMultipleNutrientFieldsSaltAndSugars_expectFiltered() {
    listingsSearchParams.setSalt(List.of(NutritionFactsLevel.LOW));
    listingsSearchParams.setSugars(List.of(NutritionFactsLevel.MODERATE));
    Specification<Listing> specification = ListingSpecifications
        .meetsSearchCriteria(listingsSearchParams);
    List<Listing> results = listingDao.findAll(specification);
    assertEquals(1, results.size());
    assertEquals(5005, results.get(0).getId());
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

    Listing aRatedListing = listingDao.getListing(5001);  // This should be in the results
    Listing eRatedListing = listingDao.getListing(5006);  // This should NOT be in the results

    // Assert
    Assertions.assertTrue(results.contains(aRatedListing));
    Assertions.assertFalse(results.contains(eRatedListing));
  }

  @Test
  @Transactional
  void resultsMeetSearchCriteriaTestFilteredByIsGlutenFree() {
    // Arrange & Act
    List<ListingSearchKeys> searchKeys = new ArrayList<>();
    searchKeys.add(ListingSearchKeys.PRODUCT_NAME);
    listingsSearchParams.setSearchKeys(searchKeys);
    listingsSearchParams.setSearchParam("");
    listingsSearchParams.setIsGlutenFree(true);
    Specification<Listing> specification = ListingSpecifications
        .meetsSearchCriteria(listingsSearchParams);
    List<Listing> results = listingDao.findAll(specification);

    // Assert
    for (Listing listing : results) {
      Assertions.assertTrue(listing.getInventoryItem().getProduct().getIsGlutenFree());
    }
    Assertions.assertEquals(1, results.size());
  }

  @Test
  @Transactional
  void resultsMeetSearchCriteriaTestFilteredByIsPalmOilFree() {
    // Arrange & Act
    List<ListingSearchKeys> searchKeys = new ArrayList<>();
    searchKeys.add(ListingSearchKeys.PRODUCT_NAME);
    listingsSearchParams.setSearchKeys(searchKeys);
    listingsSearchParams.setSearchParam("");
    listingsSearchParams.setIsPalmOilFree(true);
    Specification<Listing> specification = ListingSpecifications
        .meetsSearchCriteria(listingsSearchParams);
    List<Listing> results = listingDao.findAll(specification);
    // Assert
    for (Listing listing : results) {
      System.out.println(listing);
      Assertions.assertTrue(listing.getInventoryItem().getProduct().getIsPalmOilFree());
    }

    Assertions.assertTrue(
        results.stream().map(el -> el.getId()).collect(Collectors.toSet()).containsAll(
                List.of(5001l, 5002l, 5003l, 5004l)
        )
    );
  }

  @Test
  @Transactional
  void resultsMeetSearchCriteriaTestFilteredByIsDairyFree() {
    // Arrange & Act
    List<ListingSearchKeys> searchKeys = new ArrayList<>();
    searchKeys.add(ListingSearchKeys.PRODUCT_NAME);
    listingsSearchParams.setSearchKeys(searchKeys);
    listingsSearchParams.setSearchParam("");
    listingsSearchParams.setIsDairyFree(true);
    Specification<Listing> specification = ListingSpecifications
        .meetsSearchCriteria(listingsSearchParams);
    List<Listing> results = listingDao.findAll(specification);

    // Assert
    for (Listing listing : results) {
      Assertions.assertTrue(listing.getInventoryItem().getProduct().getIsDairyFree());
    }
    Assertions.assertEquals(1, results.size());
  }

  @Test
  @Transactional
  void resultsMeetSearchCriteriaTestFilteredByIsVegan() {
    // Arrange & Act
    List<ListingSearchKeys> searchKeys = new ArrayList<>();
    searchKeys.add(ListingSearchKeys.PRODUCT_NAME);
    listingsSearchParams.setSearchKeys(searchKeys);
    listingsSearchParams.setSearchParam("");
    listingsSearchParams.setIsVegan(true);
    Specification<Listing> specification = ListingSpecifications
        .meetsSearchCriteria(listingsSearchParams);
    List<Listing> results = listingDao.findAll(specification);

    // Assert
    for (Listing listing : results) {
      Assertions.assertTrue(listing.getInventoryItem().getProduct().getIsVegan());
    }
    Assertions.assertEquals(1, results.size());
  }

  @Test
  @Transactional
  void resultsMeetSearchCriteriaTestFilteredByIsVegetarian() {
    // Arrange & Act
    List<ListingSearchKeys> searchKeys = new ArrayList<>();
    searchKeys.add(ListingSearchKeys.PRODUCT_NAME);
    listingsSearchParams.setSearchKeys(searchKeys);
    listingsSearchParams.setSearchParam("");
    listingsSearchParams.setIsVegetarian(true);
    Specification<Listing> specification = ListingSpecifications
        .meetsSearchCriteria(listingsSearchParams);
    List<Listing> results = listingDao.findAll(specification);

    // Assert
    for (Listing listing : results) {
      Assertions.assertTrue(listing.getInventoryItem().getProduct().getIsVegetarian());
    }
    Assertions.assertEquals(1, results.size());
  }
}
