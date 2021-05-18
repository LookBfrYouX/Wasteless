package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.dto.CreateInventoryItemDto;
import com.navbara_pigeons.wasteless.exception.*;
import com.navbara_pigeons.wasteless.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
  public ResponseEntity<Object> showBusinessInventory(@PathVariable long id) {
    try {
      log.info("RETRIEVED INVENTORY ITEMS FOR BUSINESS: " + id);
      return new ResponseEntity<>(this.inventoryService.getInventory(id), HttpStatus.valueOf(200));
    } catch (InsufficientPrivilegesException exc) {
      log.info("INSUFFICIENT PRIVILEGES GETTING BUSINESS WITH ID: " + id + " " + exc.getMessage());
      throw new ResponseStatusException(HttpStatus.valueOf(403), "Insufficient Privileges");
    } catch (BusinessNotFoundException | UserNotFoundException exc) {
      log.info("USER OR BUSINESS NOT FOUND: " + id + " " + exc.getMessage());
      throw new ResponseStatusException(HttpStatus.valueOf(400), exc.getMessage());
    } catch (InventoryItemNotFoundException exc) {
      log.info("PRODUCT NOT FOUND WITH ID AND BUSINESS ID: " + id + " " + exc.getMessage());
      throw new ResponseStatusException(HttpStatus.valueOf(400), exc.getMessage());
    }  catch (Exception exc) {
      log.info("EXCEPTION GETTING INVENTORY " + exc.getMessage());
      throw new ResponseStatusException(HttpStatus.valueOf(500), "Internal Error");
    }
  }

  @PostMapping("/businesses/{id}/inventory")
  public ResponseEntity<Object> addToBusinessInventory(@PathVariable long id, @RequestBody CreateInventoryItemDto inventoryDto) {
    try {
      log.info("ADDED NEW INVENTORY ITEM FOR PRODUCT id: " + inventoryDto.getProductId() + " FOR BUSINESS: " + id);
      return new ResponseEntity<>(this.inventoryService.addInventoryItem(id, inventoryDto), HttpStatus.valueOf(201));
    } catch (InventoryRegistrationException exc) {
      log.info("ADDING NEW INVENTORY_ITEM, BUSINESS ID " + id + " ERROR " + exc.getMessage());
      throw new ResponseStatusException(HttpStatus.valueOf(400),
              "There was some error with the data supplied by the user");
    } catch (InventoryItemForbiddenException exc) {
      log.info("INSUFFICIENT PRIVILEGES GETTING BUSINESS WITH ID " + id + " " + exc.getMessage());
      throw new ResponseStatusException(HttpStatus.valueOf(403), "Insufficient Privileges");
    } catch (ProductNotFoundException exc) {
      log.info("PRODUCT NOT FOUND WITH ID AND BUSINESS ID: " + id + " " + exc.getMessage());
      throw new ResponseStatusException(HttpStatus.valueOf(400), exc.getMessage());
    } catch (Exception exc) {
      log.info("EXCEPTION GETTING INVENTORY + " + exc.getMessage());
      throw new ResponseStatusException(HttpStatus.valueOf(500), "Internal Error");
    }
  }
// sorry Max we don't know how to cast these wizadry magic spells to do this have fun.
//  @PostMapping("/businesses/{id}/inventory")
//  @ResponseStatus(value = HttpStatus.CREATED)
//  public void registerInventoryItem(@PathVariable long id, @RequestBody CreateInventoryItemDto inventoryItemDto) throws InsufficientPrivilegesException {
//    this.inventoryService.registerInventoryItem(inventoryItemDto);
//  }

}
