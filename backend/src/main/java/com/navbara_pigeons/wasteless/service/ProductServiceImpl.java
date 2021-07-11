package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dao.ProductDao;
import com.navbara_pigeons.wasteless.dto.BasicProductCreationDto;
import com.navbara_pigeons.wasteless.dto.BasicProductDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Currency;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.ForbiddenException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.ProductNotFoundException;
import com.navbara_pigeons.wasteless.exception.ProductRegistrationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import com.navbara_pigeons.wasteless.validation.ProductServiceValidation;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Class for dealing with all business logic to do with products
 */
@Service
public class ProductServiceImpl implements ProductService {

  private final BusinessDao businessDao;
  private final ProductDao productDao;
  private final CountryDataFetcherService countryDataFetcherService;
  private final UserService userService;
  private final BusinessService businessService;
  @Value("${public_path_prefix}")
  private String publicPathPrefix;

  /**
   * ProductImplementation constructor that takes autowired parameters and sets up the service for
   * interacting with all business related services.
   *
   * @param businessDao               The BusinessDataAccessObject.
   * @param countryDataFetcherService
   */
  @Autowired
  public ProductServiceImpl(BusinessDao businessDao, ProductDao productDao,
      UserService userService, BusinessService businessService,
      CountryDataFetcherService countryDataFetcherService) {
    this.businessDao = businessDao;
    this.productDao = productDao;
    this.userService = userService;
    this.businessService = businessService;
    this.countryDataFetcherService = countryDataFetcherService;
  }

  /**
   * This method retrieves a list of all the products listed by a specific business from the
   * ProductDao given the business ID.
   *
   * @param businessId    The ID of the business whose products are to be retrieved.
   * @param pagStartIndex The start index of the list to return, implemented for pagination, Can be Null
   * @param pagEndIndex   The stop index of the list to return, implemented for pagination, Can be Null
   * @param sortBy        Defines any product sorting needed and the direction (ascending or
   *                      descending). In the format "fieldName-<acs/desc>", Can be Null
   * @return productCatalogue A List<Product> of products that are in the business product
   * catalogue.
   * @throws BusinessNotFoundException If the business is not listed in the database.
   */
  @Override
  public List<BasicProductDto> getProducts(long businessId, Integer pagStartIndex,
      Integer pagEndIndex, String sortBy)
      throws BusinessNotFoundException, InsufficientPrivilegesException, UserNotFoundException {
    if (!this.userService.isAdmin() && !this.businessService.isBusinessAdmin(businessId)) {
      throw new InsufficientPrivilegesException("You are not permitted to modify this business");
    }

    Business business = businessDao.getBusinessById(businessId);

    PaginationBuilder pagBuilder = new PaginationBuilder(Product.class, "id");
    pagBuilder.withPagStartIndex(pagStartIndex)
        .withPagEndIndex(pagEndIndex)
        .withSortByString(sortBy);

    List<Product> serverResults = productDao.getProducts(business, pagBuilder);

    ArrayList<BasicProductDto> products = new ArrayList<>();
    for (Product product : serverResults) {
      products.add(new BasicProductDto(product, publicPathPrefix));
    }
    return products;
  }

  /**
   * This method adds a new product to a specific business catalogue
   *
   * @param businessId   The ID of the business.
   * @param basicProduct basic product details for the product to be added.
   * @return JSONObject with `productId`
   * @throws ProductRegistrationException If data supplied is not expected (bad request)
   * @throws ForbiddenException           If user if not an admin of the business (forbidden)
   * @throws ForbiddenException           If user if not an admin of the business (forbidden)
   */
  @Override
  @Transactional
  public JSONObject addProduct(long businessId, BasicProductCreationDto basicProduct)
      throws ProductRegistrationException,
      InsufficientPrivilegesException {
    // Throw 400 if bad request, 403 if user is not business admin
    Business business;
    try {
      business = businessDao.getBusinessById(businessId);
      if (!businessService.isBusinessAdmin(businessId) && !userService.isAdmin()) {
        throw new InsufficientPrivilegesException(
            "User does not have permission to add a product to the business");
      }
    } catch (UserNotFoundException | BusinessNotFoundException exc) {
      throw new ProductRegistrationException("User or business not found");
    }

    if (!ProductServiceValidation.priceIsValid(basicProduct.getRecommendedRetailPrice())) {
      // Needs to be here as basicProduct stores Double; product stores double
      throw new ProductRegistrationException(
          "Invalid price; must be a number greater than 0");
    }

    Product product = new Product();

    product.setName(basicProduct.getName());
    product.setDescription(basicProduct.getDescription());
    product.setRecommendedRetailPrice(basicProduct.getRecommendedRetailPrice());
    product.setManufacturer(basicProduct.getManufacturer());

    Currency currency = countryDataFetcherService
        .getCurrencyForCountry(business.getAddress().getCountry());
    if (currency == null) {
      throw new ProductRegistrationException("Unknown country; cannot set currency");
    }
    product.setCurrency(currency.getCode());

    if (!ProductServiceValidation.requiredFieldsNotEmpty(product)) {
      throw new ProductRegistrationException("Required fields not given or were empty");
    }

    product.setCreated(ZonedDateTime.now(ZoneOffset.UTC));
    productDao.saveProduct(product);

    // add product to catalogue table
    business.addCatalogueProduct(product);
    businessDao.saveBusiness(business);

    JSONObject response = new JSONObject();
    response.appendField("productId", product.getId());
    return response;
  }

  @Override
  public Product getProduct(long productId) throws ProductNotFoundException {
    return productDao.getProduct(productId);
  }

  @Transactional
  public void saveProduct(Product product) {
    productDao.saveProduct(product);
  }

}
