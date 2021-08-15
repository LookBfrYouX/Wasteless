package com.navbara_pigeons.wasteless.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dao.InventoryDao;
import com.navbara_pigeons.wasteless.dao.ProductDao;
import com.navbara_pigeons.wasteless.dao.UserDao;
import com.navbara_pigeons.wasteless.dto.BasicInventoryItemDto;
import com.navbara_pigeons.wasteless.dto.CreateInventoryItemDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.exception.InventoryItemNotFoundException;
import com.navbara_pigeons.wasteless.exception.InventoryRegistrationException;
import com.navbara_pigeons.wasteless.exception.InventoryUpdateException;
import com.navbara_pigeons.wasteless.exception.ProductNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.util.Pair;
import org.springframework.security.test.context.support.WithMockUser;

class InventoryServiceImplTest extends ServiceTestProvider {

  @Mock
  UserDao userDaoMock;
  @Mock
  BusinessDao businessDaoMock;
  @Mock
  UserService userServiceMock;
  @Mock
  BusinessService businessServiceMock;
  @Mock
  ProductDao productDaoMock;
  @Mock
  ProductService productServiceMock;
  @Mock
  InventoryDao inventoryDaoMock;
  @Mock
  ListingService listingServiceMock;
  @InjectMocks
  InventoryServiceImpl inventoryService;

  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  void getInventory_isBusinessAdmin()
      throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException, InsufficientPrivilegesException, InvalidPaginationInputException {
    User user = makeUser(EMAIL_1, PASSWORD_1, false);
    user.setId(100);
    when(userDaoMock.getUserById(user.getId())).thenReturn(user);
    when(userServiceMock.isAdmin()).thenReturn(false);

    Business business = makeBusiness(BUSINESS_1_NAME, user);
    business.setId(101);
    when(businessDaoMock.getBusinessById(business.getId())).thenReturn(business);
    when(businessServiceMock.isBusinessAdmin(business.getId())).thenReturn(true);

    user.addBusiness(business);

    Product product = makeProduct(PRODUCT_1_NAME);
    product.setId(102);
    when(productDaoMock.getProduct(product.getId())).thenReturn(product);

    business.addCatalogueProduct(product);

    InventoryItem inventoryItem = makeInventoryItem(product, business);
    inventoryItem.setId(103);

    business.getInventory().add(inventoryItem);

    List<InventoryItem> inventory = new ArrayList<>();
    inventory.add(inventoryItem);

    when(inventoryDaoMock.getInventoryItems(any(Business.class), any(PaginationBuilder.class)))
        .thenReturn(Pair.of(inventory, 0L));

    List<BasicInventoryItemDto> inventoryDto = inventoryService
        .getInventory(business.getId(), null, null, null, true).getResults();

    assertInventoryListEquals(inventory, inventoryDto);
  }

  @Test
  @WithMockUser(username = EMAIL_2)
  void getInventory_isNotBusinessAdmin() {
    assertThrows(InsufficientPrivilegesException.class,
        () -> inventoryService.getInventory(1, null, null, null, true));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN"})
  void getInventory_isAdmin()
      throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException, InsufficientPrivilegesException, InvalidPaginationInputException {
    User user = makeUser(EMAIL_1, PASSWORD_1, false);
    user.setId(100);
    when(userDaoMock.getUserById(user.getId())).thenReturn(user);
    when(userServiceMock.isAdmin()).thenReturn(true);

    Business business = makeBusiness(BUSINESS_1_NAME, user);
    business.setId(101);
    when(businessDaoMock.getBusinessById(business.getId())).thenReturn(business);
    when(businessServiceMock.isBusinessAdmin(business.getId())).thenReturn(false);

    user.addBusiness(business);

    Product product = makeProduct(PRODUCT_1_NAME);
    product.setId(102);
    when(productDaoMock.getProduct(product.getId())).thenReturn(product);

    business.addCatalogueProduct(product);

    InventoryItem inventoryItem = makeInventoryItem(product, business);
    inventoryItem.setId(103);

    business.getInventory().add(inventoryItem);

    List<InventoryItem> inventoryItemList = new ArrayList<>();
    inventoryItemList.add(inventoryItem);

    when(inventoryDaoMock.getInventoryItems(any(Business.class), any(PaginationBuilder.class)))
        .thenReturn(Pair.of(inventoryItemList, 0L));

    List<BasicInventoryItemDto> inventory = inventoryService
        .getInventory(business.getId(), null, null, null, true).getResults();

    assertInventoryListEquals(inventoryItemList, inventory);
  }

  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  void getInventory_notFound() throws BusinessNotFoundException, UserNotFoundException {
    when(userServiceMock.isAdmin()).thenReturn(false);
    when(businessServiceMock.isBusinessAdmin(1000)).thenThrow(BusinessNotFoundException.class);
    assertThrows(BusinessNotFoundException.class,
        () -> inventoryService.getInventory(1000, null, null, null, true));
  }

  void makeInventoryTestWrapper(CreateInventoryItemDto item, boolean shouldFail)
      throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException {
    Business business = makeBusiness().setId(2);
    Product product = makeProduct("ame").setId(1);

    doNothing().when(inventoryDaoMock).saveInventoryItem(any(InventoryItem.class));
    when(businessServiceMock.getBusiness(business.getId())).thenReturn(business);
    when(productServiceMock.getProduct(product.getId())).thenReturn(product);
    when(businessServiceMock.isBusinessAdmin(business.getId())).thenReturn(true);
    item.setProductId(product.getId());

    if (shouldFail) {
      Assertions.assertThrows(InventoryRegistrationException.class, () ->
          inventoryService.addInventoryItem(business.getId(), item)
      );
    } else {
      Assertions
          .assertDoesNotThrow(() -> inventoryService.addInventoryItem(business.getId(), item));
    }
  }

  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  void invalidMakeInventory_bestBeforeBeforeManufactured()
      throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException {
    CreateInventoryItemDto item = new CreateInventoryItemDto();
    item.setManufactured(LocalDate.now().minusDays(2));
    item.setBestBefore(LocalDate.now().minusDays(3));
    makeInventoryTestWrapper(item, true);
  }

  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  void invalidMakeInventory_sellByBeforeBestBefore()
      throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException {
    CreateInventoryItemDto item = new CreateInventoryItemDto();
    item.setSellBy(LocalDate.now().minusDays(2));
    item.setBestBefore(LocalDate.now().minusDays(1));
    makeInventoryTestWrapper(item, true);
  }

  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  void invalidMakeInventory_expiryBeforeSellBy()
      throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException {
    CreateInventoryItemDto item = new CreateInventoryItemDto();
    item.setExpires(LocalDate.now().minusDays(2));
    item.setSellBy(LocalDate.now().minusDays(1));
    makeInventoryTestWrapper(item, true);
  }

  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  void invalidMakeInventory_bestBeforeBeforeExpiry()
      throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException {
    CreateInventoryItemDto item = new CreateInventoryItemDto();
    item.setBestBefore(LocalDate.now().plusDays(4));
    item.setExpires(LocalDate.now().plusDays(3));
    makeInventoryTestWrapper(item, true);
  }

  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  void makeInventory_allDatesAreToday()
      throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException {
    CreateInventoryItemDto item = new CreateInventoryItemDto();
    item.setExpires(LocalDate.now());
    item.setSellBy(LocalDate.now());
    item.setBestBefore(LocalDate.now());
    item.setManufactured(LocalDate.now());
    makeInventoryTestWrapper(item, false);
  }

  /**
   * Helper method for testing the update inventory item method (note the items quantity is 5)
   *
   * @param quantity   quantity to remove from the inventory item
   * @param shouldFail boolean to determine whether an exception should be thrown or not
   */
  void updateInventoryTestWrapper(Long quantity, boolean shouldFail)
      throws BusinessNotFoundException {
    Long mockBusinessId = 2L;
    Long mockInventoryId = 2L;
    Long mockInventoryQuantity = 5L;

    Product mockProduct = makeProduct("Cheese");
    Business mockBusiness = makeBusiness("Tony's Cheese");
    mockBusiness.setId(mockBusinessId);
    InventoryItem mockInventoryItem = makeInventoryItem(mockProduct, mockBusiness);
    mockInventoryItem.setQuantity(mockInventoryQuantity);
    mockInventoryItem.setId(mockInventoryId);
    mockInventoryItem.setListings(Collections.emptyList());
    mockBusiness.addInventoryItem(mockInventoryItem);

    when(businessDaoMock.getBusinessById(mockBusinessId)).thenReturn(mockBusiness);
    doNothing().when(listingServiceMock).deleteListing(any(Long.class));
    doNothing().when(inventoryDaoMock).deleteInventoryItem(any(InventoryItem.class));
    doNothing().when(inventoryDaoMock).saveInventoryItem(any(InventoryItem.class));

    if (shouldFail) {
      assertThrows(InventoryUpdateException.class, () -> inventoryService
          .updateInventoryItemQuantity(mockBusinessId, mockInventoryId, quantity));
    } else {
      assertDoesNotThrow(() -> inventoryService
          .updateInventoryItemQuantity(mockBusinessId, mockInventoryId, quantity));
    }
  }

  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  void updateInventory_quantityLessThanZero()
      throws BusinessNotFoundException {
    updateInventoryTestWrapper(10L, true);
  }

  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  void updateInventory_quantityEqualToZero()
      throws BusinessNotFoundException {
    updateInventoryTestWrapper(5L, false);
  }

  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  void updateInventory_quantityGreaterThenZero()
      throws BusinessNotFoundException {
    updateInventoryTestWrapper(1L, false);
  }

  /**
   * Tests that the inventory item is passed to the DAO delete layer - all tests mock DAO so can't
   * really test if DAO layer works in these tests
   */
  @Test
  void deleteInventoryItem_sanityCheck() {
    InventoryItem item = new InventoryItem()
        .setProduct(makeProduct("ame").setId(1))
        .setBusiness(makeBusiness().setId(2))
        .setId(3);

    doNothing().when(inventoryDaoMock).deleteInventoryItem(any(InventoryItem.class));
    inventoryService.deleteInventoryItem(item);

    Mockito.verify(inventoryDaoMock).deleteInventoryItem(item);
  }
}
