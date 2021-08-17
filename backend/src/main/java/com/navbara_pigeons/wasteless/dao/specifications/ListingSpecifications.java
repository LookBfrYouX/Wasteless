package com.navbara_pigeons.wasteless.dao.specifications;

import com.navbara_pigeons.wasteless.entity.*;
import com.navbara_pigeons.wasteless.model.ListingsSearchParams;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.IntFunction;
import javax.management.InvalidAttributeValueException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public class ListingSpecifications {

  private ListingSpecifications() {}

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
            predicates.add(getListingFullMatch(params, currentToken.replace("\"",""), inventoryItemJoin, productInventoryItemJoin, businessInventoryItemJoin, addressBusinessJoin, criteriaBuilder));
          } else {
            predicates.add(getListingPartialMatch(params, currentToken, inventoryItemJoin, productInventoryItemJoin, businessInventoryItemJoin, addressBusinessJoin, criteriaBuilder));
          }
          // Check if OR Operator
        } else if (currentToken.toUpperCase().matches("OR") && predicates.size() > 0 && tokenIterator.hasNext()) {
          String nextToken = tokenIterator.next();
          Predicate lastPredicate = predicates.remove(predicates.size() - 1);
          if (SpecificationHelper.isFullMatching(nextToken)) {
            predicates.add(criteriaBuilder.or(lastPredicate, getListingFullMatch(params, nextToken.replace("\"",""), inventoryItemJoin, productInventoryItemJoin, businessInventoryItemJoin, addressBusinessJoin, criteriaBuilder)));
          } else {
            predicates.add(criteriaBuilder.or(lastPredicate, getListingPartialMatch(params, nextToken, inventoryItemJoin, productInventoryItemJoin, businessInventoryItemJoin, addressBusinessJoin, criteriaBuilder)));
          }
          // Check if AND Operator
        } else if (currentToken.toUpperCase().matches("AND") && predicates.size() > 0 && tokenIterator.hasNext()) {
          String nextToken = tokenIterator.next();
          if (SpecificationHelper.isFullMatching(nextToken)) {
            predicates.add(getListingFullMatch(params, nextToken.replace("\"",""), inventoryItemJoin, productInventoryItemJoin, businessInventoryItemJoin, addressBusinessJoin, criteriaBuilder));
          } else {
            predicates.add(getListingPartialMatch(params, nextToken, inventoryItemJoin, productInventoryItemJoin, businessInventoryItemJoin, addressBusinessJoin, criteriaBuilder));
          }
        } else {
          System.out.println("Check your syntax");
        }
      }
      predicates.add(getListingFilterMatch(params, root, businessInventoryItemJoin, criteriaBuilder));
      return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    };
  }

  private static Predicate getListingPartialMatch(ListingsSearchParams params, String token,  Join<Listing, InventoryItem> inventoryItemJoin, Join<Product, InventoryItem> productInventoryItemJoin, Join<Business, InventoryItem> businessInventoryItemJoin, Join<Address, Business> addressBusinessJoin, CriteriaBuilder criteriaBuilder ) {
    ArrayList<Predicate> predicates = new ArrayList<>();
    if (params.getSearchKeys().contains("Product Name")) {
      predicates.add(criteriaBuilder.like(productInventoryItemJoin.get("name"), "%" + token + "%"));
    }
    if (params.getSearchKeys().contains("Business Name")) {
      predicates.add(criteriaBuilder.like(businessInventoryItemJoin.get("name"), "%" + token + "%"));
    }
    if (params.getSearchKeys().contains("Address")) {
      Predicate countrySearch = criteriaBuilder.like(addressBusinessJoin.get("country"), "%" + token + "%");
      Predicate citySearch = criteriaBuilder.like(addressBusinessJoin.get("city"), "%" + token + "%");
      predicates.add(criteriaBuilder.or(countrySearch, citySearch));
    }
    return criteriaBuilder.or(predicates.toArray(new Predicate[predicates.size()]));
  }

  private static Predicate getListingFullMatch(ListingsSearchParams params, String token, Join<Listing, InventoryItem> inventoryItemJoin, Join<Product, InventoryItem> productInventoryItemJoin, Join<Business, InventoryItem> businessInventoryItemJoin, Join<Address, Business> addressBusinessJoin, CriteriaBuilder criteriaBuilder ) {
    ArrayList<Predicate> predicates = new ArrayList<>();
    if (params.getSearchKeys().contains("Product Name")) {
      predicates.add(criteriaBuilder.like(productInventoryItemJoin.get("name"),token));
    }
    if (params.getSearchKeys().contains("Business Name")) {
      predicates.add(criteriaBuilder.like(businessInventoryItemJoin.get("name"), token));
    }
    if (params.getSearchKeys().contains("Address")) {
      Predicate countrySearch = criteriaBuilder.like(addressBusinessJoin.get("country"), token );
      Predicate citySearch = criteriaBuilder.like(addressBusinessJoin.get("city"), token );
      predicates.add(criteriaBuilder.or(countrySearch, citySearch));
    }
    return criteriaBuilder.or(predicates.toArray(new Predicate[predicates.size()]));
  }

  private static Predicate getListingFilterMatch(ListingsSearchParams params, Root<Listing> root, Join<Business, InventoryItem> businessInventoryItemJoin, CriteriaBuilder criteriaBuilder) {
    ArrayList<Predicate> predicates = new ArrayList<>();
    if( params.getMinPrice() != null ) {
      predicates.add(criteriaBuilder.greaterThan(root.get("price"), params.getMinPrice()));
    }
    if( params.getMaxPrice() != null ) {
      predicates.add(criteriaBuilder.lessThan(root.get("price"), params.getMaxPrice()));
    }

    return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));

  }

}
