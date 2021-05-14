package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.InventoryItemNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * This controller class provides the endpoints for dealing with inventory items.
 * All requests for inventory items listed by businesses are received here.
 */
@Controller
@Slf4j
@RequestMapping("")
public class InventoryController {

  private final InventoryService inventoryService;

  @Autowired
  public InventoryController(InventoryService inventoryService) {
    this.inventoryService = inventoryService;
  }

  /**
   * This endpoint retrieves a list of all products listed by a particular business (id).
   *
   * @param id The ID of the business whose inventory is to be displayed
   * @return response A JSONObject containing the information of all inventory items listed for the
   * business.
   */
  @GetMapping("/businesses/{id}/inventory")
  public ResponseEntity<Object> showBusinessInventory(@PathVariable long id) throws UserNotFoundException, InsufficientPrivilegesException, InventoryItemNotFoundException, BusinessNotFoundException {
    ResponseEntity<Object> response = new ResponseEntity<>(this.inventoryService.getInventory(id), HttpStatus.valueOf(200));
    log.info("RETRIEVED INVENTORY ITEMS FOR BUSINESS: " + id);
    return response;
  }

  @PostMapping("/businesses/{id}/inventory")
  public ResponseEntity<Object> registerInventoryItem(@PathVariable long id, @RequestBody JSONObject object) throws InsufficientPrivilegesException {
    throw new InsufficientPrivilegesException();
  }

}
