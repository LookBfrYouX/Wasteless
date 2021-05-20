package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.ListingDao;
import com.navbara_pigeons.wasteless.dto.CreateListingDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.ForbiddenException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class ListingServiceImplTest extends ServiceTestProvider {

  @InjectMocks
  private ListingServiceImpl listingService;
  @Mock
  private BusinessService businessService;
  @Mock
  ListingDao listingDao;
  @Mock
  UserService userService;
  @Mock
  InventoryService inventoryService;

  private final String email = "tony@tony.tony";
  private final String password = "tonyTony1";
  private Long userId;
  private Long businessId;

  @BeforeEach
  void beforeEach()
      throws UserNotFoundException, BusinessNotFoundException {
    // Setting mocks before tests are run to ensure unit testing only
    User user = makeUser(email, password, false);
    userId = user.getId();
    Business business = makeBusiness();
    businessId = business.getId();
    business.setPrimaryAdministratorId(userId);

    when(userService.isAdmin()).thenReturn(false);
    when(businessService.isBusinessAdmin(businessId)).thenReturn(true);
    doNothing().when(listingDao).save(any(Listing.class));
  }

  Product newProduct(long id, Business business) {
    Product product = new Product();
    business.addCatalogueProduct(product);
    // product.setBusiness(business); // Product does not store business ID
    product.setId(id);
    return product;
  }

  InventoryItem newInventory(long id, Product product, Business business) {
    InventoryItem inventory = new InventoryItem();
    business.getInventory().add(inventory);
    inventory.setProduct(product);
    inventory.setId(id);
    return inventory;
  }


  Listing newListing(long id, InventoryItem inventory) {
    Listing listing = new Listing();
    inventory.addListing(listing);
    listing.setInventoryItem(inventory);
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
    InventoryItem i1 = newInventory(1, p1, business);
    Listing l1 = newListing(1, i1);
    Listing l2 = newListing(2, i1);
    InventoryItem i2 = newInventory(2, p1, business);
    Listing l3 = newListing(3, i2);

    Product p2 = newProduct(2, business);
    InventoryItem i3 = newInventory(3, p2, business);
    Listing l4 = newListing(4, i3);
    Listing l5 = newListing(5, i3);
    Listing l6 = newListing(6, i3);
    return business;
  }

  @Test
  void getListings_one_product_multiple_inventory_multiple_listings()
      throws UserNotFoundException, BusinessNotFoundException {
    when(businessService.getBusiness(Mockito.anyLong())).thenReturn(getMockBusiness());
    Assertions.assertArrayEquals(
        listingService.getListings(1).stream().map(listing -> listing.getId()).toArray(),
        List.of(1, 2, 3, 4, 5, 6).stream().map(id -> Long.valueOf(id)).toArray()
    );
  }

  @Test
  @WithMockUser(username = email, password = password)
  void postListingExpectOk() {
    CreateListingDto listing = makeListing();

    Assertions.assertDoesNotThrow(() -> {
      listingService.addListing(businessId, listing);
    });
  }

  @Test
  @WithMockUser(username = email, password = password)
  void postListingExpectInvalid() {
    // Listings must have quantities otherwise throw ListingValidationException
    CreateListingDto listing = makeListing();

    Assertions.assertThrows(ListingValidationException.class, () -> {
      listingService.addListing(businessId, listing);
    });
  }

  @Test
  @WithMockUser(username = "notTony@notTony.notTony", password = "notTonyNotTony1")
  void postListingExpectForbidden() throws BusinessNotFoundException, UserNotFoundException {
    // Must be the business admin or GAA otherwise throw ForbiddenException
    CreateListingDto listing = makeListing();

    // Setting mocks
    when(businessService.isBusinessAdmin(businessId)).thenReturn(false);

    Assertions.assertThrows(ForbiddenException.class, () -> {
      listingService.addListing(businessId, listing);
    });
  }

  private CreateListingDto makeListing() {
    Business business = makeBusiness();

    Product product = new Product();
    product.setName("Some product");

    InventoryItem inventoryItem = new InventoryItem();
    inventoryItem.setProduct(product);
    inventoryItem.setQuantity(4);
    inventoryItem.setExpires(LocalDate.parse("2021-07-21"));
    inventoryItem.setBusiness(business);

    CreateListingDto listing = new CreateListingDto();
    listing.setInventoryItemId(inventoryItem.getId());
    listing.setQuantity(4);
    listing.setPrice(17.99f);
    listing.setId(47);

    try {
      when(inventoryService.getInventoryItemById(businessId, 47)).thenReturn(inventoryItem);
    } catch (Exception exc) {
      exc.printStackTrace();
    }

    return listing;
  }
}
