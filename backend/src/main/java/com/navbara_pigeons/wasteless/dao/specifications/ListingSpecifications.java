package com.navbara_pigeons.wasteless.dao.specifications;

import com.navbara_pigeons.wasteless.entity.*;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.model.ListingsSearchParams;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.IntFunction;
import javax.management.InvalidAttributeValueException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import com.navbara_pigeons.wasteless.enums.ListingSearchKeys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import static com.navbara_pigeons.wasteless.enums.ListingSearchKeys.*;

@Slf4j
public class ListingSpecifications {

  private ListingSpecifications() {}

  /**
  * Method to check whether an of the values in the database matches the search criteria sent by the user
  * @param params search query and other request parameters
   */

  public static Specification<Listing> meetsSearchCriteria(ListingsSearchParams params) {
    return (root, query, criteriaBuilder) -> {

      Join<Listing, InventoryItem> inventoryItemJoin = root.join("inventoryItem");
      Join<Product, InventoryItem> productInventoryItemJoin = inventoryItemJoin.join("product");
      Join<Business, InventoryItem> businessInventoryItemJoin = inventoryItemJoin.join("business");
      Join<Address, Business> addressBusinessJoin = businessInventoryItemJoin.join("address");
      ArrayList<Predicate> predicates = new ArrayList<>();
      Iterator<String> tokenIterator = SpecificationHelper.tokenize(params.getSearchParam()).iterator();
      while (tokenIterator.hasNext()) {
        String currentToken = tokenIterator.next();
        // First Query token
        if (predicates.isEmpty()) {
          if (SpecificationHelper.isFullMatching(currentToken)) {
            predicates.add(getListingFullMatch(params, currentToken.replace("\"",""), productInventoryItemJoin, businessInventoryItemJoin, addressBusinessJoin, criteriaBuilder));
          } else {
            predicates.add(getListingPartialMatch(params, currentToken, productInventoryItemJoin, businessInventoryItemJoin, addressBusinessJoin, criteriaBuilder));
          }
          // Check if OR Operator
        } else if (currentToken.toUpperCase().matches("OR") && predicates.size() > 0 && tokenIterator.hasNext()) {
          String nextToken = tokenIterator.next();
          Predicate lastPredicate = predicates.remove(predicates.size() - 1);
          if (SpecificationHelper.isFullMatching(nextToken)) {
            predicates.add(criteriaBuilder.or(lastPredicate, getListingFullMatch(params, nextToken.replace("\"",""), productInventoryItemJoin, businessInventoryItemJoin, addressBusinessJoin, criteriaBuilder)));
          } else {
            predicates.add(criteriaBuilder.or(lastPredicate, getListingPartialMatch(params, nextToken, productInventoryItemJoin, businessInventoryItemJoin, addressBusinessJoin, criteriaBuilder)));
          }
          // Check if AND Operator
        } else if (currentToken.toUpperCase().matches("AND") && predicates.size() > 0 && tokenIterator.hasNext()) {
          String nextToken = tokenIterator.next();
          if (SpecificationHelper.isFullMatching(nextToken)) {
            predicates.add(getListingFullMatch(params, nextToken.replace("\"",""), productInventoryItemJoin, businessInventoryItemJoin, addressBusinessJoin, criteriaBuilder));
          } else {
            predicates.add(getListingPartialMatch(params, nextToken, productInventoryItemJoin, businessInventoryItemJoin, addressBusinessJoin, criteriaBuilder));
          }
        } else {
          log.error("Malformed Search Query");
        }
      }
      predicates.add(getListingFilterMatch(params, root, businessInventoryItemJoin, criteriaBuilder));
      return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    };
  }

  /**
   * Method to check whether an of the values in the database partially matches the search criteria sent by the user
   * @param params search query and other request parameters
   * @param token that the predicate is being made for
   * @param productInventoryItemJoin join inventoryitem table with product table
   * @param businessInventoryItemJoin join inventoryitem table with business table
   * @param addressBusinessJoin join business table with address table
   */
  private static Predicate getListingPartialMatch(ListingsSearchParams params, String token, Join<Product, InventoryItem> productInventoryItemJoin, Join<Business, InventoryItem> businessInventoryItemJoin, Join<Address, Business> addressBusinessJoin, CriteriaBuilder criteriaBuilder ) {
    ArrayList<Predicate> predicates = new ArrayList<>();
    // if user searches by product name
    if (params.getSearchKeys().contains(PRODUCT_NAME.toString())) {
      predicates.add(criteriaBuilder.like(productInventoryItemJoin.get("name"), "%" + token + "%"));
    }
    // if user searches by business name
    if (params.getSearchKeys().contains(BUSINESS_NAME.toString())) {
      predicates.add(criteriaBuilder.like(businessInventoryItemJoin.get("name"), "%" + token + "%"));
    }
    // if user searches by address name
    if (params.getSearchKeys().contains(ADDRESS.toString())) {
      // creates predicate for the address being a country
      Predicate countrySearch = criteriaBuilder.like(addressBusinessJoin.get("country"), "%" + token + "%");
      // creates predicate for the address being a city
      Predicate citySearch = criteriaBuilder.like(addressBusinessJoin.get("city"), "%" + token + "%");
      // creates predicate for being a city or a country
      predicates.add(criteriaBuilder.or(countrySearch, citySearch));
    }
    return criteriaBuilder.or(predicates.toArray(new Predicate[predicates.size()]));
  }

  /**
   * Method to check whether an of the values in the database fully matches the search criteria sent by the user
   * @param params search query and other request parameters
   * @param token that the predicate is being made for
   * @param productInventoryItemJoin join inventoryitem table with product table
   * @param businessInventoryItemJoin join inventoryitem table with business table
   * @param addressBusinessJoin join business table with address table
   */
  private static Predicate getListingFullMatch(ListingsSearchParams params, String token, Join<Product, InventoryItem> productInventoryItemJoin, Join<Business, InventoryItem> businessInventoryItemJoin, Join<Address, Business> addressBusinessJoin, CriteriaBuilder criteriaBuilder ) {
    ArrayList<Predicate> predicates = new ArrayList<>();
    // user searches by product name
    if (params.getSearchKeys().contains("Product Name")) {
      predicates.add(criteriaBuilder.like(productInventoryItemJoin.get("name"),token));
    }
    // user searches by business name
    if (params.getSearchKeys().contains("Business Name")) {
      predicates.add(criteriaBuilder.like(businessInventoryItemJoin.get("name"), token));
    }
    // user searches by address name
    if (params.getSearchKeys().contains("Address")) {
      Predicate countrySearch = criteriaBuilder.like(addressBusinessJoin.get("country"), token );
      Predicate citySearch = criteriaBuilder.like(addressBusinessJoin.get("city"), token );
      predicates.add(criteriaBuilder.or(countrySearch, citySearch));
    }
    return criteriaBuilder.or(predicates.toArray(new Predicate[predicates.size()]));
  }

  /**
   * Method to create filters for the listings
   * @param params search query and other request parameters
   * @param root the root table
   * @param businessInventoryItemJoin join inventoryitem table with business table
   * @param criteriaBuilder to build the predicates
   */
  private static Predicate getListingFilterMatch(ListingsSearchParams params, Root<Listing> root, Join<Business, InventoryItem> businessInventoryItemJoin, CriteriaBuilder criteriaBuilder) {
    ArrayList<Predicate> predicates = new ArrayList<>();
    // if min price included by the user
    if( params.getMinPrice() != null ) {
      predicates.add(criteriaBuilder.greaterThan(root.get("price"), params.getMinPrice()));
    }
    // if max price included by the user
    if( params.getMaxPrice() != null ) {
      predicates.add(criteriaBuilder.lessThan(root.get("price"), params.getMaxPrice()));
    }
    // if one date included it is taken as the max date
    if (params.getFilterDates() != null && params.getFilterDates().size() == 1) {
      predicates.add(criteriaBuilder.lessThan(root.get("created"), params.getFilterDates().get(0)));
    // if two dates included it is taken as a date range
    } else if (params.getFilterDates() != null && params.getFilterDates().size() == 2) {
      predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThan(root.get("created"), params.getFilterDates().get(0)), criteriaBuilder.lessThan(root.get("created"), params.getFilterDates().get(1))));
    }
    // if business types are included by the user
    if (params.getBusinessTypes() != null) {
      for (int i = 0; i < params.getBusinessTypes().size(); i++ ) {
        predicates.add(criteriaBuilder.like(businessInventoryItemJoin.get("businessType"), params.getBusinessTypes().get(i).toString()));
      }
    }
    return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
  }

}
