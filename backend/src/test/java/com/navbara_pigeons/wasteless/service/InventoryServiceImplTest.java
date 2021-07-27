package com.navbara_pigeons.wasteless.service;

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
import com.navbara_pigeons.wasteless.exception.*;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.util.Pair;
import org.springframework.security.test.context.support.WithMockUser;

public class InventoryServiceImplTest extends ServiceTestProvider {

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

  @InjectMocks
  InventoryServiceImpl inventoryService;

  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  public void getInventory_isBusinessAdmin()
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
  public void getInventory_isNotBusinessAdmin() {
    assertThrows(InsufficientPrivilegesException.class,
        () -> inventoryService.getInventory(1, null, null, null, true));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN"})
  public void getInventory_isAdmin()
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
  public void getInventory_notFound() throws BusinessNotFoundException, UserNotFoundException {
    when(userServiceMock.isAdmin()).thenReturn(false);
    when(businessServiceMock.isBusinessAdmin(1000)).thenThrow(BusinessNotFoundException.class);
    assertThrows(BusinessNotFoundException.class,
        () -> inventoryService.getInventory(1000, null, null, null, true));
  }


  public void makeInventoryTestWrapper(CreateInventoryItemDto item, boolean shouldFail) throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException {
    Business business = makeBusiness().setId(2);
    Product product = makeProduct("ame").setId(1);

    doNothing().when(inventoryDaoMock).saveInventoryItem(any(InventoryItem.class));
    when(businessServiceMock.getBusinessById(business.getId())).thenReturn(business);
    when(productServiceMock.getProduct(product.getId())).thenReturn(product);
    when(businessServiceMock.isBusinessAdmin(business.getId())).thenReturn(true);
    item.setProductId(product.getId());

    if (shouldFail) {
      Assertions.assertThrows(InventoryRegistrationException.class, () ->
          inventoryService.addInventoryItem(business.getId(), item)
      );
    } else {
      Assertions.assertDoesNotThrow(() -> inventoryService.addInventoryItem(business.getId(), item));
    }
  }

  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  public void invalidMakeInventory_bestBeforeBeforeManufactured() throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException {
    CreateInventoryItemDto item = new CreateInventoryItemDto();
    item.setManufactured(LocalDate.now().minusDays(2));
    item.setBestBefore(LocalDate.now().minusDays(3));
    makeInventoryTestWrapper(item, true);
  }

  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  public void invalidMakeInventory_sellByBeforeBestBefore() throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException {
    CreateInventoryItemDto item = new CreateInventoryItemDto();
    item.setSellBy(LocalDate.now().minusDays(2));
    item.setBestBefore(LocalDate.now().minusDays(1));
    makeInventoryTestWrapper(item, true);
  }


  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  public void invalidMakeInventory_expiryBeforeSellBy() throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException {
    CreateInventoryItemDto item = new CreateInventoryItemDto();
    item.setExpires(LocalDate.now().minusDays(2));
    item.setSellBy(LocalDate.now().minusDays(1));
    makeInventoryTestWrapper(item, true);
  }


  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  public void invalidMakeInventory_bestBeforeBeforeExpiry() throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException {
    CreateInventoryItemDto item = new CreateInventoryItemDto();
    item.setBestBefore(LocalDate.now().plusDays(4));
    item.setExpires(LocalDate.now().plusDays(3));
    makeInventoryTestWrapper(item, true);
  }


  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  public void makeInventory_allDatesAreToday() throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException {
    CreateInventoryItemDto item = new CreateInventoryItemDto();
    item.setExpires(LocalDate.now());
    item.setSellBy(LocalDate.now());
    item.setBestBefore(LocalDate.now());
    item.setManufactured(LocalDate.now());
    makeInventoryTestWrapper(item, false);
  }
}
