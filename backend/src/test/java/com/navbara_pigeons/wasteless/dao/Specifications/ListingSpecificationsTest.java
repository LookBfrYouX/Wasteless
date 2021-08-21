package com.navbara_pigeons.wasteless.dao.Specifications;

import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dao.InventoryDao;
import com.navbara_pigeons.wasteless.dao.ListingDao;
import com.navbara_pigeons.wasteless.dao.ProductDao;
import com.navbara_pigeons.wasteless.dao.specifications.ListingSpecifications;
import com.navbara_pigeons.wasteless.entity.*;
import com.navbara_pigeons.wasteless.enums.ListingSearchKeys;
import com.navbara_pigeons.wasteless.exception.InventoryItemNotFoundException;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.model.ListingsSearchParams;
import com.navbara_pigeons.wasteless.service.InventoryService;
import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;


@SpringBootTest
public class ListingSpecificationsTest extends MainTestProvider {


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
    void resultsMeetSearchCriteriaTestPartialMatching() throws ListingValidationException, InventoryItemNotFoundException {
        List<String> searchKeys = new ArrayList<String>();
        searchKeys.add("Product Name");
        listingsSearchParams.setSearchKeys(searchKeys);
        listingsSearchParams.setSearchParam("Sanitarium");
        Specification<Listing> specification = ListingSpecifications.meetsSearchCriteria(listingsSearchParams);
        List<Listing> results = listingDao.findAll(specification);
        assertEquals(5003, results.get(0).getId());
    }

    @Test
    void resultsMeetSearchCriteriaTestFullMatching() throws ListingValidationException, InventoryItemNotFoundException {
        List<String> searchKeys = new ArrayList<String>();
        searchKeys.add("Product Name");
        listingsSearchParams.setSearchKeys(searchKeys);
        listingsSearchParams.setSearchParam("\"Sanitarium So Good Oat Milk No Added Sugar\"");
        Specification<Listing> specification = ListingSpecifications.meetsSearchCriteria(listingsSearchParams);
        List<Listing> results = listingDao.findAll(specification);
        assertEquals(5004, results.get(0).getId());
    }

}
