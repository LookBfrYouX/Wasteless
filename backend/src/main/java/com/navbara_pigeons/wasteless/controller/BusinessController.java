package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.dto.CreateBusinessDto;
import com.navbara_pigeons.wasteless.dto.UserIdDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.exception.AddressValidationException;
import com.navbara_pigeons.wasteless.exception.BusinessAdminException;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.BusinessRegistrationException;
import com.navbara_pigeons.wasteless.exception.BusinessTypeException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.service.BusinessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

/**
 * This controller class provides the endpoints for dealing with businesses. All requests for
 * information about businesses are received here. IMPORTANT NOTE: Endpoints for information about
 * the products listed by businesses can be found in the ProductController class.
 */
@RestController
@Slf4j
@RequestMapping("")
@Tag(name = "Business Endpoint", description = "The API endpoint for Business related requests")
public class BusinessController {

  private final BusinessService businessService;

  public BusinessController(@Autowired BusinessService businessService) {
    this.businessService = businessService;
  }

  /**
   * Endpoint allowing a user to register a business account Returns error with message if service
   * business logic doesnt pass
   *
   * @param business The Business object that is sent from the front-end.
   * @return Returns the newly created businesses id and 201 status code in a ResponseEntity
   * @throws ResponseStatusException Unknown Error.
   */
  @PostMapping("/businesses")
  @Operation(summary = "Register business", description = "Register a business using the 'CreateBusinessDto' object")
  public ResponseEntity<JSONObject> registerBusiness(@RequestBody @Valid CreateBusinessDto business)
      throws UserNotFoundException, AddressValidationException, BusinessTypeException, BusinessRegistrationException {
    JSONObject businessId = businessService.saveBusiness(new Business(business));
    log.info("BUSINESS CREATED SUCCESSFULLY: " + businessId.get("businessId"));
    return new ResponseEntity<>(businessId, HttpStatus.CREATED);
  }

  /**
   * Search for a specific business using the id field.
   *
   * @param id unique identifier of the business being searched for
   * @return business entity matching the given id
   * @throws ResponseStatusException HTTP 401 Unauthorised & 406 Not Acceptable
   */
  @GetMapping("/businesses/{id}")
  @Operation(summary = "Get information about a business", description = "Returns all information about a business with the given id")
  public ResponseEntity<Object> getBusinessById(@PathVariable String id)
          throws UserNotFoundException, BusinessNotFoundException {
    log.info("GETTING BUSINESS BY ID: " + id);
    return new ResponseEntity<>(
        businessService.getBusinessById(Long.parseLong(id)), HttpStatus.OK);
  }

  /**
   * Add a specific user to the list of administrators for a business
   *
   * @param businessId unique identifier of the business being searched for
   * @param userIdDto     the id of the user to add to the list of admins
   */
  @PutMapping("/businesses/{businessId}/makeAdministrator")
  @Operation(summary = "Make user a business admin", description = "Add another user to the list of admins for a business")
  public ResponseEntity<String> addBusinessAdmin(@PathVariable String businessId,
      @RequestBody UserIdDto userIdDto)
      throws UserNotFoundException, InsufficientPrivilegesException, BusinessNotFoundException {
    log.info("ADDING USER WITH ID " + userIdDto.getUserId() + " AS ADMIN TO BUSINESS WITH ID: " + businessId);
    businessService.addBusinessAdmin(Long.parseLong(businessId), userIdDto.getUserId());
    return new ResponseEntity<>("Individual added as an administrator successfully",
        HttpStatus.OK);
  }

  /**
   * Removes a specific user from the list of administrators for a business
   *
   * @param businessId unique identifier of the business being searched for
   * @param userIdDto     the id of the user to be removed from the list of admins
   */
  @PutMapping("/businesses/{businessId}/removeAdministrator")
  @Operation(summary = "Remove a users business admin privileges", description = "Remove user from list of admins for a business")
  public ResponseEntity<String> removeBusinessAdmin(@PathVariable String businessId,
      @RequestBody UserIdDto userIdDto)
      throws UserNotFoundException, InsufficientPrivilegesException, BusinessNotFoundException, BusinessAdminException {
    log.info("REMOVING USER WITH ID " + userIdDto.getUserId() + " FROM LIST OF ADMINS IN BUSINESS WITH ID: "
        + businessId);
    businessService.removeBusinessAdmin(Long.parseLong(businessId), userIdDto.getUserId());
    return new ResponseEntity<>("Individual added as an administrator successfully",
        HttpStatus.OK);
  }
}
