package com.navbara_pigeons.wasteless.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.navbara_pigeons.wasteless.dao.ListingDao;
import com.navbara_pigeons.wasteless.dto.FullListingDto;
import com.navbara_pigeons.wasteless.dto.TransactionDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.Transaction;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.enums.ListingSortByOption;
import com.navbara_pigeons.wasteless.exception.BusinessAndListingMismatchException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.InventoryUpdateException;
import com.navbara_pigeons.wasteless.exception.ListingNotFoundException;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import com.navbara_pigeons.wasteless.model.ListingsSearchParams;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.security.test.context.support.WithMockUser;

class ListingServiceImplTest extends ServiceTestProvider {

  private final String email = "tony@tony.tony";
  private final String password = "tonyTony1";
  @Mock
  private UserService userService;
  @Mock
  private InventoryService inventoryService;
  @Mock
  private ListingDao listingDao;
  @Mock
  private BusinessService businessService;
  @Mock
  private TransactionService transactionService;
  @InjectMocks
  private ListingServiceImpl listingService;
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
  void getExistingListing() throws Exception {
    // Arrange
    Listing listing = makeListing();
    when(listingDao.getListing(anyLong())).thenReturn(listing);

    // Act
    Listing returnedListing = listingService.getListing(listing.getId());

    // Assert
    Assertions.assertEquals(listing, returnedListing);
  }

  @Test
  void getNonExistingListing() throws Exception {
    // Arrange
    when(listingDao.getListing(anyLong())).thenThrow(ListingNotFoundException.class);

    // Act & Assert
    Assertions.assertThrows(
        ListingNotFoundException.class,
        () -> listingService.getListing(123)
    );
  }

  @Test
  void getListings_one_product_multiple_inventory_multiple_listings() throws Exception {
    Business mockBusiness = getMockBusiness();
    when(businessService.getBusiness(anyLong())).thenReturn(mockBusiness);
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

  @Test
  void deleteListingExpectOk() {
    doNothing().when(listingDao).deleteListing(any(Long.class));

    Assertions.assertDoesNotThrow(() -> {
      listingService.deleteListing(1L); // ListingId doesnt matter here (dao being mocked)
    });
  }

  @Test
  void purchaseListingWithValidParameters()
      throws Exception {
    // Arrange
    Listing listing = makeListing();
    Business business = listing.getInventoryItem().getBusiness();
    Long transactionId = 123L;
    when(listingDao.getListing(anyLong())).thenReturn(listing);
    // Because the saveTransaction method returns void but also sets the transaction ID this
    // is how you mock setting the ID.
    doAnswer(invocation -> {
      Object[] args = invocation.getArguments();
      ((Transaction) args[0]).setId(transactionId);
      return null; // void method, so return null
    }).when(transactionService).saveTransaction(any(Transaction.class));

    // Act
    TransactionDto transactionDto = listingService
        .purchaseListing(business.getId(), listing.getId());

    // Assert
    Assertions.assertEquals(transactionId, transactionDto.getTransactionId());
  }

  @Test
  void purchaseListingFromMismatchBusiness() throws Exception {
    // Arrange
    Listing listing = makeListing();
    Business business = listing.getInventoryItem().getBusiness();
    when(listingDao.getListing(anyLong())).thenReturn(listing);

    // Act & Assert
    Assertions.assertThrows(BusinessAndListingMismatchException.class,
        () -> listingService.purchaseListing(business.getId() + 1, listing.getId())
    );
  }

  @Test
  void purchaseListingWithNonExistingListingId() throws Exception {
    // Arrange
    Listing listing = makeListing();
    Business business = listing.getInventoryItem().getBusiness();
    when(listingDao.getListing(anyLong())).thenThrow(ListingNotFoundException.class);

    // Act & Assert
    Assertions.assertThrows(
        ListingNotFoundException.class,
        () -> listingService.purchaseListing(business.getId(), listing.getId())
    );
  }

  @Test
  void purchaseListingWithInvalidQuantityRemaining() throws Exception {
    // Arrange
    Listing listing = makeListing();
    Business business = listing.getInventoryItem().getBusiness();
    when(listingDao.getListing(anyLong())).thenReturn(listing);
    doThrow(InventoryUpdateException.class).when(inventoryService)
        .updateInventoryItemFromPurchase(anyLong(), any(Listing.class));

    // Act & Assert
    Assertions.assertThrows(
        InventoryUpdateException.class,
        () -> listingService.purchaseListing(business.getId(), listing.getId())
    );
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
  @WithMockUser(username = "ntony@tony.tony", password = "tonyTony1")
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
      business.setPrimaryAdministratorId(1L);
      Product product = makeProduct(mockProductNames.get(i));
      InventoryItem inventoryItem = makeInventoryItem(product, business);
      Listing listing = makeListing(inventoryItem);
      mockListings.add(listing);
    }


    ListingsSearchParams listingsSearchParams = new ListingsSearchParams();
    listingsSearchParams.setPagStartIndex(0);
    listingsSearchParams.setPagEndIndex(1);
    listingsSearchParams.setSortBy(ListingSortByOption.NAME);

    when(listingDao
        .findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<Listing>(mockListings));

    Assertions.assertEquals(3, listingService.searchListings(listingsSearchParams).getTotalCount());
  }
}
