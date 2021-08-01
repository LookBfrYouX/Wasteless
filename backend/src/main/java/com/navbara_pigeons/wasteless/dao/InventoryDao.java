package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import java.util.List;
import org.springframework.data.util.Pair;

public interface InventoryDao {

  void saveInventoryItem(InventoryItem inventory);

  Pair<List<InventoryItem>, Long> getInventoryItems(Business business, PaginationBuilder pagBuilder)
      throws InvalidPaginationInputException;
}
