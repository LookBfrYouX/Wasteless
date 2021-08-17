package com.navbara_pigeons.wasteless.dao.specifications;

import com.navbara_pigeons.wasteless.entity.*;
import com.navbara_pigeons.wasteless.model.ListingsSearchParams;
import java.util.ArrayList;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
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
      if (SpecificationHelper.isFullMatching(params.getSearchParam())) {
        String token = params.getSearchParam().replace("\"","");
        predicates.add(getListingFullMatch(params, token, inventoryItemJoin, productInventoryItemJoin, businessInventoryItemJoin, addressBusinessJoin, criteriaBuilder));
      } else {
        String token = params.getSearchParam();
        predicates.add(getListingPartialMatch(params, token, inventoryItemJoin, productInventoryItemJoin, businessInventoryItemJoin, addressBusinessJoin, criteriaBuilder));
      }
      return criteriaBuilder.or(predicates.toArray(new Predicate[predicates.size()]));
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

  private static Predicate getListingFullMatch(ListingsSearchParams params, String token,  Join<Listing, InventoryItem> inventoryItemJoin, Join<Product, InventoryItem> productInventoryItemJoin, Join<Business, InventoryItem> businessInventoryItemJoin, Join<Address, Business> addressBusinessJoin, CriteriaBuilder criteriaBuilder ) {
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

}
