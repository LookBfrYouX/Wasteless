package com.navbara_pigeons.wasteless.testprovider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import com.navbara_pigeons.wasteless.dto.BasicAddressDto;
import com.navbara_pigeons.wasteless.dto.BasicBusinessDto;
import com.navbara_pigeons.wasteless.dto.BasicInventoryItemDto;
import com.navbara_pigeons.wasteless.dto.BasicProductDto;
import com.navbara_pigeons.wasteless.dto.BasicUserDto;
import com.navbara_pigeons.wasteless.dto.FullAddressDto;
import com.navbara_pigeons.wasteless.dto.FullBusinessDto;
import com.navbara_pigeons.wasteless.dto.FullUserDto;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.Keyword;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import com.navbara_pigeons.wasteless.service.BusinessService;
import com.navbara_pigeons.wasteless.service.KeywordService;
import com.navbara_pigeons.wasteless.service.ProductService;
import com.navbara_pigeons.wasteless.service.UserService;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class provides the setup for Service level tests and provides various convenience methods.
 */
public class ServiceTestProvider extends MainTestProvider {

  @Autowired
  protected KeywordService keywordService;

  @Autowired
  protected UserService userService;

  @Autowired
  protected BusinessService businessService;

  @Autowired
  protected ProductService productService;


  /**
   * Logs in with a default set of credentials
   */
  protected void loginWithCredentials() {
    loginWithCredentials("dnb36@uclive.ac.nz", "fun123");
  }

  /**
   * Logs in with the given credentials
   *
   * @param email
   * @param password
   */
  protected void loginWithCredentials(String email, String password) {
    UserCredentials userCredentials = new UserCredentials();
    userCredentials.setEmail(email);
    userCredentials.setPassword(password);
    try {
      userService.login(userCredentials);
    } catch (Exception e) {
      Assertions.fail(e);
    }
  }

  /**
   * Asserts that an inventory and its DTO's are equal
   *
   * @param inventory
   * @param inventoryDto
   */
  protected void assertInventoryListEquals(List<InventoryItem> inventory,
      List<BasicInventoryItemDto> inventoryDto) {
    inventory.sort(Comparator.comparing(InventoryItem::getId));
    inventoryDto.sort(Comparator.comparing(BasicInventoryItemDto::getId));
    assertEquals(inventory.size(), inventoryDto.size());
    for (int i = 0; i < inventory.size(); i++) {
      assertInventoryEquals(inventory.get(i), inventoryDto.get(i));
    }
  }

  /**
   * Asserts that a given inventory and its DTO are equal
   *
   * @param inventory
   * @param inventoryDto
   */
  protected void assertInventoryEquals(InventoryItem inventory,
      BasicInventoryItemDto inventoryDto) {
    assertEquals(inventory.getId(), inventoryDto.getId());
    assertProductEquals(inventory.getProduct(), inventoryDto.getProduct());
    assertEquals(inventory.getQuantity(), inventoryDto.getQuantity());
    assertEquals(inventory.getPricePerItem(), inventoryDto.getPricePerItem());
    assertEquals(inventory.getManufactured(), inventoryDto.getManufactured());
    assertEquals(inventory.getSellBy(), inventoryDto.getSellBy());
    assertEquals(inventory.getBestBefore(), inventoryDto.getBestBefore());
    assertEquals(inventory.getExpires(), inventoryDto.getExpires());
  }

  /**
   * Asserts that a business and its DTO are equal
   *
   * @param business
   * @param businessDto
   */
  protected void assertBusinessEquals(Business business, FullBusinessDto businessDto) {
    assertEquals(business.getId(), businessDto.getId());
    assertEquals(business.getName(), businessDto.getName());
    assertEquals(business.getDescription(), businessDto.getDescription());
    assertEquals(business.getCreated(), businessDto.getCreated());
    assertEquals(business.getBusinessType(), businessDto.getBusinessType());
    assertAddressEquals(business.getAddress(), businessDto.getAddress());
    assertEquals(business.getPrimaryAdministratorId(), businessDto.getPrimaryAdministratorId());

    // admin list
    assertUsersList(business.getAdministrators(), businessDto.getAdministrators());
  }

  /**
   * Asserts that a business and its DTO are equal
   *
   * @param business
   * @param businessDto
   */
  protected void assertBusinessEquals(Business business, BasicBusinessDto businessDto) {
    assertEquals(business.getId(), businessDto.getId());
    assertEquals(business.getName(), businessDto.getName());
    assertEquals(business.getDescription(), businessDto.getDescription());
    assertEquals(business.getCreated(), businessDto.getCreated());
    assertEquals(business.getBusinessType(), businessDto.getBusinessType());

    assertEquals(business.getPrimaryAdministratorId(), businessDto.getPrimaryAdministratorId());
    assertAddressEquals(business.getAddress(), businessDto.getAddress());
  }


  /**
   * Assert list of users matches given list. Order does not matter
   *
   * @param users
   * @param userDtos
   */
  protected void assertUsersList(List<User> users, List<?> userDtos) {
    HashSet<Long> usersNotFoundYet = new HashSet<>();
    users.forEach(user -> usersNotFoundYet.add(user.getId()));

    for (Object userObject : userDtos) {
      long id = -1;
      if (userObject instanceof FullUserDto) {
        id = ((FullUserDto) userObject).getId();
      } else if (userObject instanceof BasicUserDto) {
        id = ((BasicUserDto) userObject).getId();
      } else {
        fail("Not user DTO object");
      }

      long finalId = id;
      Optional<User> expectedUsersDto = users.stream()
          .filter(business -> business.getId() == finalId).findFirst();
      if (expectedUsersDto.isEmpty()) {
        fail("Received user with id " + id + "; not in original list");
      }
      User expectedUser = expectedUsersDto.get();

      if (userObject instanceof FullUserDto) {
        assertUserEquals(expectedUser, (FullUserDto) userObject);
      } else {
        assertUserEquals(expectedUser, (BasicUserDto) userObject);
      }
      usersNotFoundYet.remove(expectedUser.getId());
    }

    if (usersNotFoundYet.size() > 0) {
      fail("Businesses that were in original list not found");
    }
  }

  /**
   * Given list of businesses, checks if all business match. Order does not matter
   *
   * @param businesses
   * @param businessDtos
   */
  protected void assertBusinessList(List<Business> businesses, List<?> businessDtos) {
    businesses.sort(Comparator.comparing(bus -> bus.getId()));

    businessDtos.sort(Comparator.comparing(bus -> {
      if (bus instanceof FullBusinessDto) {
        return ((FullBusinessDto) bus).getId();
      } else {
        return ((BasicBusinessDto) bus).getId();
      }
    }));

    assertEquals(businesses.size(), businessDtos.size(),
        "business and business DTOs list different length");

    for (int i = 0; i < businesses.size(); i++) {
      if (businessDtos.get(i) instanceof FullBusinessDto) {
        assertBusinessEquals(
            businesses.get(i), (FullBusinessDto) businessDtos.get(i));
      } else {
        assertBusinessEquals(businesses.get(i), (BasicBusinessDto) businessDtos.get(i));
      }
    }
  }

  /**
   * Asserts that a user matches a user DTO
   *
   * @param user
   * @param userDto
   */
  protected void assertUserEquals(User user, FullUserDto userDto) {
    assertEquals(user.getId(), userDto.getId());
    assertEquals(user.getFirstName(), userDto.getFirstName());
    assertEquals(user.getMiddleName(), userDto.getMiddleName());
    assertEquals(user.getLastName(), userDto.getLastName());
    assertEquals(user.getNickname(), userDto.getNickname());

    assertEquals(user.getBio(), userDto.getBio());
    assertEquals(user.getCreated(), userDto.getCreated());
    assertEquals(user.getRole(), userDto.getRole());

    assertEquals(user.getEmail(), userDto.getEmail());
    assertEquals(user.getDateOfBirth(), userDto.getDateOfBirth());
    assertEquals(user.getPhoneNumber(), userDto.getPhoneNumber());

    assertAddressEquals(user.getHomeAddress(), userDto.getHomeAddress());

    assertBusinessList(user.getBusinesses(), userDto.getBusinesses());
  }

  /**
   * Asserts that a user matches a user DTO
   *
   * @param user
   * @param userDto
   */
  protected void assertUserEquals(User user, BasicUserDto userDto) {
    assertEquals(user.getId(), userDto.getId());
    assertEquals(user.getFirstName(), userDto.getFirstName());
    assertEquals(user.getMiddleName(), userDto.getMiddleName());
    assertEquals(user.getLastName(), userDto.getLastName());
    assertEquals(user.getNickname(), userDto.getNickname());

    assertEquals(user.getBio(), userDto.getBio());
    assertEquals(user.getCreated(), userDto.getCreated());
    assertEquals(user.getRole(), userDto.getRole());

    assertAddressEquals(user.getHomeAddress(), userDto.getHomeAddress());
    assertBusinessList(user.getBusinesses(), userDto.getBusinesses());
  }

  /**
   * Asserts an address matches its DTO
   *
   * @param address
   * @param addressDto
   */
  protected void assertAddressEquals(Address address, BasicAddressDto addressDto) {
    assertEquals(address.getCity(), addressDto.getCity());
    assertEquals(address.getRegion(), addressDto.getRegion());
    assertEquals(address.getCountry(), addressDto.getCountry());
  }

  /**
   * Asserts an address matches its DTO
   *
   * @param address
   * @param addressDto
   */
  protected void assertAddressEquals(Address address, FullAddressDto addressDto) {
    assertEquals(address.getStreetNumber(), addressDto.getStreetNumber());
    assertEquals(address.getStreetName(), addressDto.getStreetName());
    assertEquals(address.getPostcode(), addressDto.getPostcode());
    assertEquals(address.getCity(), addressDto.getCity());
    assertEquals(address.getRegion(), addressDto.getRegion());
    assertEquals(address.getCountry(), addressDto.getCountry());
  }

  protected void assertProductEquals(Product product, BasicProductDto productDto) {
    assertEquals(product.getCreated(), productDto.getCreated());
    assertEquals(product.getName(), productDto.getName());
    assertEquals(product.getId(), productDto.getId());
    assertEquals(product.getManufacturer(), productDto.getManufacturer());
    assertEquals(product.getRecommendedRetailPrice(), productDto.getRecommendedRetailPrice());
  }

  Product newProduct(long id, Business business) {
    Product product = new Product();
    business.addCatalogueProduct(product);
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

  public Business getMockBusiness() {
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

  /**
   * Asserts all Keyword names in a list of Keyword.
   *
   * @param keywords List of Keyword
   * @param expectedKeywords List of Keyword
   */
  protected void assertKeywordEquals(List<Keyword> keywords, List<Keyword> expectedKeywords) {
    for (int i = 0; i < keywords.size(); i++) {
      assertEquals(keywords.get(i).getName(), expectedKeywords.get(i).getName());
    }
  }
}
