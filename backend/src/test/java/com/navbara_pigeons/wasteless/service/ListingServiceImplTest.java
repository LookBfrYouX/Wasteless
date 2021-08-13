package com.navbara_pigeons.wasteless.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.navbara_pigeons.wasteless.dao.ListingDao;
import com.navbara_pigeons.wasteless.dto.FullListingDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.BusinessType;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.enums.ListingSortByOption;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.util.Pair;
import org.springframework.security.test.context.support.WithMockUser;

public class ListingServiceImplTest extends ServiceTestProvider {

  private final String email = "tony@tony.tony";
  private final String password = "tonyTony1";
  @Mock
  UserService userService;
  @Mock
  InventoryService inventoryService;
  @Mock
  ListingDao listingDao;
  @InjectMocks
  private ListingServiceImpl listingService;
  @Mock
  private BusinessService businessService;
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
  }

  @Test
  void getListings_one_product_multiple_inventory_multiple_listings() throws Exception {
    Business mockBusiness = getMockBusiness();
    when(businessService.getBusiness(Mockito.anyLong())).thenReturn(mockBusiness);
    when(listingDao.getListings(any(Business.class), any(PaginationBuilder.class)))
        .thenReturn(Pair.of(getMockBusinessListings(mockBusiness), 0L));

    Assertions.assertArrayEquals(
        List.of(1, 2, 3, 4, 5, 6).stream().map(Long::valueOf).toArray(),
        listingService.getListings(mockBusiness.getId(), null, null, null, true)
            .getResults()
            .stream().map(FullListingDto::getId)
            .toArray()
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
    listing.setPrice(17.99);
    listing.setId(47);

    inventoryItem.addListing(listing);

    when(inventoryService.getInventoryItemById(businessId, inventoryItem.getId()))
        .thenReturn(inventoryItem);
    return listing;
  }

  @Test
  @WithMockUser(username = "notTony@notTony.notTony", password = "notTonyNotTony1")
  void getAllListings_isAuthenticated() throws Exception {

    List<Listing> mockListings = new ArrayList<>();

    List<String> mockBusinessNames = new ArrayList<>();
    mockBusinessNames.add("Business A");
    mockBusinessNames.add("Business B");
    mockBusinessNames.add("Business C");

    List<String> mockProductNames = new ArrayList<String>();
    mockProductNames.add("Product A");
    mockProductNames.add("Product B");
    mockProductNames.add("Product C");

    for (int i = 0; i < 3; i++) {
      Business business = makeBusiness(mockBusinessNames.get(i));
      Product product = makeProduct(mockProductNames.get(i));
      InventoryItem inventoryItem = makeInventoryItem(product, business);
      Listing listing = makeListing(inventoryItem);
      mockListings.add(listing);
    }

    when(listingDao
        .searchAllListings(anyList(), anyString(), anyDouble(), anyDouble(), anyList(), anyList(),
            any(PaginationBuilder.class)))
        .thenReturn(Pair.of(mockListings, 10L));

    Assertions.assertEquals(listingService.searchListings(
        1, 10, ListingSortByOption.name, true,
        new ArrayList<String>(), "searchValue", 20D, 80D,
        new ArrayList<LocalDate>(), new ArrayList<BusinessType>()).getTotalCount(), 10);
  }
}
