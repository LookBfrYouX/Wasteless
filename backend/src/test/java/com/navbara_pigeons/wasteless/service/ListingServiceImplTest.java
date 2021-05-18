package com.navbara_pigeons.wasteless.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import com.navbara_pigeons.wasteless.dao.ListingDao;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.ForbiddenException;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithMockUser;

public class ListingServiceImplTest extends ServiceTestProvider {

  @Mock
  ListingDao listingDao;
  @Mock
  UserService userService;
  @Mock
  BusinessService businessService;
  @InjectMocks
  ListingServiceImpl listingService;

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
    doNothing().when(listingDao).saveListing(any(Listing.class));
  }

  @Test
  @WithMockUser(username = email, password = password)
  void postListingExpectOk() {
    Listing listing = makeListing();

    Assertions.assertDoesNotThrow(() -> {
      listingService.addListing(businessId, listing);
    });
  }

  @Test
  @WithMockUser(username = email, password = password)
  void postListingExpectInvalid() {
    // Listings must have quantities otherwise throw ListingValidationException
    Listing listing = makeListing();

    Assertions.assertThrows(ListingValidationException.class, () -> {
      listingService.addListing(businessId, listing);
    });
  }

  @Test
  @WithMockUser(username = "notTony@notTony.notTony", password = "notTonyNotTony1")
  void postListingExpectForbidden() throws BusinessNotFoundException, UserNotFoundException {
    // Must be the business admin or GAA otherwise throw ForbiddenException
    Listing listing = makeListing();

    // Setting mocks
    when(businessService.isBusinessAdmin(businessId)).thenReturn(false);

    Assertions.assertThrows(ForbiddenException.class, () -> {
      listingService.addListing(businessId, listing);
    });
  }

  private Listing makeListing() {
    // TODO: mock the inventory item here
    Product product = new Product();
    product.setName("Some product");

    InventoryItem inventoryItem = new InventoryItem();
    inventoryItem.setProduct(product);
    inventoryItem.setQuantity(4);
    inventoryItem.setExpires(LocalDate.parse("2021-07-21"));

    Listing listing = new Listing();
    listing.setInventoryItem(inventoryItem);
    listing.setQuantity(4);
    listing.setPrice(17.99f);

    return listing;
  }
}
