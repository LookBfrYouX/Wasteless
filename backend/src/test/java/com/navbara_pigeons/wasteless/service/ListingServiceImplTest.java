package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Inventory;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.when;

public class ListingServiceImplTest extends ServiceTestProvider {
  @InjectMocks
  private ListingServiceImpl listingService;

  @Mock
  private BusinessService businessService;

  Product newProduct(long id, Business business) {
    Product product = new Product();
    business.addCatalogueProduct(product);
    // product.setBusiness(business); // Product does not store business ID
    product.setId(id);
    return product;
  }

  Inventory newInventory(long id, Product product, Business business) {
    Inventory inventory = new Inventory();
    business.getInventory().add(inventory);
    inventory.setProduct(product);
    inventory.setId(id);
    return inventory;
  }


  Listing newListing(long id, Inventory inventory) {
    Listing listing = new Listing();
    inventory.addListing(listing);
    listing.setInventory(inventory);
    listing.setId(id);
    return listing;
  }

  Business getMockBusiness() {
    /**
     *                  B#1
     *         p1#1 ---------p2#2
     *    i1#1---i2#2         i1#3
     *l1#1--l2#2  l1#3     l1#4 l2#5 l3#6
     */
    Business business = new Business();
    business.setId(1);
    Product p1 = newProduct(1, business);
    Inventory i1 = newInventory(1, p1, business);
    Listing l1 = newListing(1, i1);
    Listing l2 = newListing(2, i1);
    Inventory i2 = newInventory(2, p1, business);
    Listing l3 = newListing(3, i2);

    Product p2 = newProduct(2, business);
    Inventory i3 = newInventory(3, p2, business);
    Listing l4 = newListing(4, i3);
    Listing l5 = newListing(5, i3);
    Listing l6 = newListing(6, i3);
    return business;
  }

  @Test
  void getListings_one_product_multiple_inventory_multiple_listings() throws UserNotFoundException, BusinessNotFoundException {
    when(businessService.getBusiness(Mockito.anyLong())).thenReturn(getMockBusiness());
    Assertions.assertArrayEquals(
        listingService.getListings(1).stream().map(listing -> listing.getId()).toArray(),
        List.of(1, 2, 3, 4, 5, 6).stream().map(id -> Long.valueOf(id)).toArray()
    );
  }

}
