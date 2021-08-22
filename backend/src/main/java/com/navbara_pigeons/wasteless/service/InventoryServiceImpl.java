package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dao.InventoryDao;
import com.navbara_pigeons.wasteless.dto.BasicInventoryItemDto;
import com.navbara_pigeons.wasteless.dto.CreateInventoryItemDto;
import com.navbara_pigeons.wasteless.dto.PaginationDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.enums.InventorySortByOption;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.exception.InventoryItemNotFoundException;
import com.navbara_pigeons.wasteless.exception.InventoryRegistrationException;
import com.navbara_pigeons.wasteless.exception.InventoryUpdateException;
import com.navbara_pigeons.wasteless.exception.ProductNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import com.navbara_pigeons.wasteless.validation.InventoryServiceValidation;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

/**
 * Class for dealing with all business logic to do with Inventories
 */
@Service
public class InventoryServiceImpl implements InventoryService {

  private final BusinessDao businessDao;
  private final UserService userService;
  private final BusinessService businessService;
  private final ProductService productService;
  private final ListingService listingService;
  private final InventoryDao inventoryDao;
  @Value("${public_path_prefix}")
  private String publicPathPrefix;

  /**
   * InventoryImplementation constructor that takes autowired parameters and sets up the service for
   * interacting with all business related services.
   */
  @Autowired
  public InventoryServiceImpl(BusinessDao businessDao, UserService userService,
      BusinessService businessService, ProductService productService,
      InventoryDao inventoryDao, @Lazy ListingService listingService) {
    // Using @Lazy to prevent Circular Dependencies
    this.businessDao = businessDao;
    this.userService = userService;
    this.businessService = businessService;
    this.productService = productService;
    this.inventoryDao = inventoryDao;
    this.listingService = listingService;
  }

  /**
   * This method retrieves a list of all the products listed by a specific business from the
   * ProductDao given the business ID.
   *
   * @param businessId    The ID of the business whose products are to be retrieved.
   * @param pagStartIndex The start index of the list to return, implemented for pagination, Can be
   *                      Null. This index is inclusive.
   * @param pagEndIndex   The stop index of the list to return, implemented for pagination, Can be
   *                      Null. This index is inclusive.
   * @param sortBy        Defines the field to be sorted, can be null.
   * @param isAscending   Boolean value, whether the sort order should be in ascending order. Is not
   *                      required and defaults to True.
   * @return productCatalogue A List<Product> of products that are in the business product
   * catalogue.
   * @throws BusinessNotFoundException If the business is not listed in the database.
   */
  @Override
  public PaginationDto<BasicInventoryItemDto> getInventory(long businessId, Integer pagStartIndex,
      Integer pagEndIndex, InventorySortByOption sortBy, boolean isAscending)
      throws BusinessNotFoundException, InsufficientPrivilegesException, UserNotFoundException, IllegalArgumentException, InvalidPaginationInputException {

    if (!this.userService.isAdmin() && !this.businessService.isBusinessAdmin(businessId)) {
      throw new InsufficientPrivilegesException(
          "You are not permitted to view the Inventory of this business");
    }

    Business business = businessDao.getBusinessById(businessId);

    PaginationBuilder pagBuilder = new PaginationBuilder(InventoryItem.class, sortBy);
    pagBuilder.withPagStartIndex(pagStartIndex)
        .withPagEndIndex(pagEndIndex)
        .withSortAscending(isAscending);

    Pair<List<InventoryItem>, Long> dataAndTotalCount = inventoryDao
        .getInventoryItems(business, pagBuilder);

    ArrayList<BasicInventoryItemDto> inventory = new ArrayList<>();
    for (InventoryItem inventoryItem : dataAndTotalCount.getFirst()) {
      inventory.add(new BasicInventoryItemDto(inventoryItem, publicPathPrefix));
    }
    return new PaginationDto<>(inventory, dataAndTotalCount.getSecond());

  }

  /**
   * Returns an inventory item by a given id
   *
   * @param businessId business with inventory to query
   * @param itemId     id of the item to be retrieved
   * @return inventory item or null
   */
  @Transactional
  public InventoryItem getInventoryItemById(long businessId, long itemId)
      throws BusinessNotFoundException, InventoryItemNotFoundException {
    Business business = businessDao.getBusinessById(businessId);
    List<InventoryItem> inventory = business.getInventory();

    for (InventoryItem inventoryItem : inventory) {
      if (inventoryItem.getId() == itemId) {
        return inventoryItem;
      }
    }
    throw new InventoryItemNotFoundException(itemId);
  }

  /**
   * This method retrieves a list of all the products listed by a specific business from the
   * ProductDao given the business ID.
   *
   * @param businessId, Create The ID of the business whose products are to be retrieved.
   * @return A List<Product> of products that are in the business product catalogue.
   */
  @Override
  @Transactional
  public long addInventoryItem(long businessId, CreateInventoryItemDto inventoryItemDto)
      throws InventoryRegistrationException, InsufficientPrivilegesException {
    Business business;
    try {
      business = businessDao.getBusinessById(businessId);
      Product product;
      long productId = inventoryItemDto.getProductId();
      product = productService.getProduct(productId);
      if (!businessService.isBusinessAdmin(businessId) && !userService.isAdmin()) {
        throw new InsufficientPrivilegesException(
            "User does not have permission to add an inventory item to the business");
      }
      InventoryItem inventoryItem = new InventoryItem(inventoryItemDto);
      inventoryItem.setProduct(product);
      inventoryItem.setBusiness(business);

      if (inventoryItem.getPricePerItem() == null) {
        inventoryItem.setPricePerItem(product.getRecommendedRetailPrice());
      }
      if (inventoryItem.getTotalPrice() == null) {
        inventoryItem.setTotalPrice(inventoryItem.getPricePerItem() * inventoryItem.getQuantity());
      }
      InventoryServiceValidation.isInventoryItemValid(inventoryItem);

      inventoryDao.saveInventoryItem(inventoryItem);

      return inventoryItem.getId();

    } catch (BusinessNotFoundException | ProductNotFoundException | UserNotFoundException exc) {
      throw new InventoryRegistrationException("BUSINESS, PRODUCT OR USER NOT FOUND");
    }
  }

  /**
   * Deletes the given inventory item
   *
   * @param inventoryItem inventory item to delete
   */
  @Override
  @Transactional
  public void deleteInventoryItem(InventoryItem inventoryItem) {
    inventoryDao.deleteInventoryItem(inventoryItem);
  }

  /**
   * Updates the quantity of the inventory item. This is called when a listing has been purchased
   * and the inventory quantity needs to be updated. When the quantity of the inventory item reaches
   * zero the inventory item and all of it's listings are deleted.
   *
   * @param businessId The id of the business
   * @param listing    The listing that has been purchased
   */
  @Override
  @Transactional
  public void updateInventoryItemFromPurchase(Long businessId, Listing listing)
      throws InventoryUpdateException {

    InventoryItem inventoryItem = listing.getInventoryItem();
    inventoryItem.removeQuantity(listing.getQuantity());

    if (inventoryItem.getQuantity() < 0) {
      throw new InventoryUpdateException("Quantity cannot be less than 0");
    } else if (inventoryItem.getQuantity() == 0) {
      this.deleteInventoryItem(inventoryItem);
    } else {
      inventoryDao.saveInventoryItem(inventoryItem);
      listingService.deleteListing(listing.getId());
    }
  }

  @Override
  @Transactional
  public InventoryItem getInventoryItemById(Long inventoryItemId) throws InventoryItemNotFoundException {
    return inventoryDao.getInventoryItemById(inventoryItemId);
  }
}