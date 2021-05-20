package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.ListingDao;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.InventoryItemNotFoundException;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import java.util.List;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
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
  void beforeEach() throws Exception {
    // Setting mocks before tests are run to ensure unit testing only
    User user = makeUser(email, password, false);
    userId = user.getId();
    Business business = makeBusiness();
    businessId = business.getId();
    business.setPrimaryAdministratorId(userId);

    when(userService.isAdmin()).thenReturn(false);
    when(businessService.isBusinessAdmin(businessId)).thenReturn(true);
    when(listingDao.save(any(Listing.class))).thenReturn(null);
  }

  @Test
  void getListings_one_product_multiple_inventory_multiple_listings() throws Exception {
    when(businessService.getBusiness(Mockito.anyLong())).thenReturn(getMockBusiness());
    Assertions.assertArrayEquals(
        listingService.getListings(1).stream().map(listing -> listing.getId()).toArray(),
        List.of(1, 2, 3, 4, 5, 6).stream().map(id -> Long.valueOf(id)).toArray()
    );
  }

  @Test
  @WithMockUser(username = email, password = password)
  void postListingExpectOk() throws Exception {
    Listing listing = makeListing();

    Assertions.assertDoesNotThrow(() -> {
      listingService.addListing(businessId, listing.getInventoryItem().getId(), listing);
    });
  }

  @Test
  @WithMockUser(username = email, password = password)
  void postListingExpectValidationException() throws Exception {
    Listing listing = makeListing();
    // Make listing sets inventory item quantity to 4
    listing.setQuantity(100);

    Assertions.assertThrows(ListingValidationException.class, () -> {
      listingService.addListing(businessId, listing.getInventoryItem().getId(), listing);
    });
  }

  @Test
  @WithMockUser(username = "notTony@notTony.notTony", password = "notTonyNotTony1")
  void postListingExpectInsufficientPrivileges() throws Exception {
    Listing listing = makeListing();

    when(businessService.isBusinessAdmin(businessId)).thenReturn(false);

    Assertions.assertThrows(InsufficientPrivilegesException.class, () -> {
      listingService.addListing(businessId, listing.getInventoryItem().getId(), listing);
    });
  }

  // Creates a business, product, inventory item and listing
  private Listing makeListing() throws Exception {
    Product product = makeProduct("Some product");
    Business business = makeBusiness();
    InventoryItem inventoryItem = makeInventoryItem(product, business);

    business.addInventoryItem(inventoryItem);

    Listing listing = new Listing();
    listing.setInventoryItem(inventoryItem);
    listing.setQuantity(4);
    listing.setPrice(17.99f);
    listing.setId(47);

    inventoryItem.addListing(listing);

    when(inventoryService.getInventoryItemById(businessId, inventoryItem.getId()))
        .thenReturn(inventoryItem);
    return listing;
  }
}
