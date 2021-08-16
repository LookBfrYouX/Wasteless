package com.navbara_pigeons.wasteless.dao.specifications;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.model.ListingsSearchParams;
import java.util.ArrayList;
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
      ArrayList<Predicate> predicates = new ArrayList<>();
      if (params.getSearchKeys().contains("Product Name")) {
        predicates.add(criteriaBuilder.like(productInventoryItemJoin.get("name"), "%" + params.getSearchParam() + "%"));
      }
      if (params.getSearchKeys().contains("Business Name")) {
        predicates.add(criteriaBuilder.like(businessInventoryItemJoin.get("name"), "%" + params.getSearchParam() + "%"));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

}
