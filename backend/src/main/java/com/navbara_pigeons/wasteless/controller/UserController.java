package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.BusinessTypeException;
import com.navbara_pigeons.wasteless.exception.NotAcceptableException;
import com.navbara_pigeons.wasteless.exception.UnhandledException;
import com.navbara_pigeons.wasteless.exception.UserAlreadyExistsException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserRegistrationException;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import com.navbara_pigeons.wasteless.service.BusinessService;
import com.navbara_pigeons.wasteless.service.UserService;
import java.util.List;
import javax.management.InvalidAttributeValueException;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Maximilian Birzer, Dawson Berry, Alec Fox
 */
@Controller
@Slf4j
@RequestMapping("")
public class UserController {

  private final UserService userService;
  private final BusinessService businessService;

  public UserController(@Autowired UserService userService, BusinessService businessService) {
    this.userService = userService;
    this.businessService = businessService;
  }

  /**
   * This method exposes a 'login' endpoint and passes the userCredentials to the UserService for
   * processing.
   *
   * @param userCredentials An entity containing the email address and password.
   * @return ResponseEntity with JSONObject containing userId on authentication pass.
   * @throws ResponseStatusException HTTP 400 exception.
   */
  @PostMapping("/login")
  public ResponseEntity<JSONObject> login(@RequestBody UserCredentials userCredentials) {
    try {
      // Attempt to login and return JSON userId if successful
      JSONObject response = userService.login(userCredentials);
      log.info("SUCCESSFUL LOGIN: " + userCredentials.getEmail());
      return new ResponseEntity<>(response, HttpStatus.valueOf(200));
    } catch (AuthenticationException exc) {
      log.error("FAILED LOGIN - CREDENTIALS");
      throw new ResponseStatusException(HttpStatus.valueOf(400),
          "Failed login attempt, email or password incorrect");
    } catch (Exception exc) {
      log.error("CRITICAL LOGIN ERROR: " + exc.getMessage());
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error.");
    }
  }

  /**
   * Endpoint allowing a user to register for an account Returns error with message if service
   * business logic doesnt pass
   *
   * @param user The User object that is sent from the front-end.
   * @return Returns the newly created users id and 201 status code in a ResponseEntity
   * @throws ResponseStatusException HTTP 400, 409 exceptions.
   */
  @PostMapping("/users")
  public ResponseEntity<JSONObject> registerUser(@RequestBody User user) {
    try {
      JSONObject createdUserId = userService.saveUser(user);
      log.info("ACCOUNT CREATED SUCCESSFULLY: " + user.getEmail());
      return new ResponseEntity<>(createdUserId, HttpStatus.valueOf(201));
    } catch (UserAlreadyExistsException exc) {
      log.error("COULD NOT REGISTER USER - EMAIL ALREADY EXISTS: " + user.getEmail());
      throw new ResponseStatusException(HttpStatus.valueOf(409), "Email address already in use");
    } catch (UserRegistrationException exc) {
      log.error("COULD NOT REGISTER USER (" + exc.getMessage() + "): " + user.getEmail());
      throw new ResponseStatusException(HttpStatus.valueOf(400), "Bad Request");
    } catch (Exception exc) {
      log.error("CRITICAL REGISTER ERROR: " + exc);

      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error.");
    }
  }

  /**
   * Search for a specific user using the id field.
   *
   * @param id unique identifier of the user being searched for
   * @return user entity matching the given id
   * @throws ResponseStatusException HTTP 401 Unauthorised & 406 Not Acceptable
   */
  @GetMapping("/users/{id}")
  public ResponseEntity<JSONObject> getUserById(@PathVariable long id) {
    try {
      log.info("GETTING USER BY ID: " + id);
      return new ResponseEntity<>(userService.getUserById(id), HttpStatus.valueOf(200));
    } catch (UserNotFoundException exc) {
      log.error("USER NOT FOUND ERROR: " + id);
      throw new ResponseStatusException(HttpStatus.valueOf(406), exc.getMessage());
    } catch (UnhandledException exc) {
      log.error(exc.getMessage());
      throw new ResponseStatusException(HttpStatus.valueOf(500), "Internal Server Error");
    }
  }

  /**
   * Search for a user based of nickname or name(firstName, middleName, lastName)
   *
   * @param searchQuery name being searched for
   * @return List of all users matching the searchQuery
   * @throws ResponseStatusException Unknown Error
   */
  @GetMapping("/users/search")
  public ResponseEntity<List<User>> searchUsers(@RequestParam String searchQuery) {
    List<User> results;
    try {
      results = userService.searchUsers(searchQuery);
    } catch (InvalidAttributeValueException e) {
      log.error("INVALID SEARCH QUERY: " + searchQuery);
      throw new ResponseStatusException(HttpStatus.valueOf(500), "Invalid Search Query");
    }
    return new ResponseEntity<>(results, HttpStatus.valueOf(200));
  }

  /**
   * Give the User matching the given 'id' parameter Global Application Admin (GAA) rights.
   *
   * @param id The unique identifier of the user being given GAA rights.
   */
  @PutMapping("/users/{id}/makeAdmin")
  public ResponseEntity<String> makeUserAdmin(@PathVariable String id) {
    try {
      userService.makeUserAdmin(Integer.parseInt(id));
      log.info("ADMIN PRIVILEGES GRANTED TO: " + id);
      return new ResponseEntity<>("Action completed successfully", HttpStatus.valueOf(200));
    } catch (NumberFormatException exc) {
      log.error("INVALID ID FORMAT ERROR: " + id);
      throw new ResponseStatusException(HttpStatus.valueOf(406), "Invalid ID format");
    } catch (UserNotFoundException exc) {
      log.error("USER NOT FOUND ERROR: " + id);
      throw new ResponseStatusException(HttpStatus.valueOf(406), "The user does not exist");
    } catch (Exception exc) {
      log.error("CRITICAL MAKEADMIN ERROR: " + exc.getMessage());
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error.");
    }
  }

  /**
   * This method removes the admin role from the user with the given ID. It can only be accessed if
   * the current user has the admin role.
   *
   * @param id The ID of the user who will lose the admin role.
   * @return HttpStatus 406 for Invalid ID format or User Doesn't Exist exception.
   */
  @PutMapping("/users/{id}/revokeAdmin")
  public ResponseEntity<String> revokeAdminPermissions(@PathVariable long id) {
    try {
      userService.revokeAdmin(id);
      log.info("ADMIN PRIVILEGES REVOKED FROM: " + id);
      return new ResponseEntity<>("Action completed successfully", HttpStatus.valueOf(200));
    } catch (UserNotFoundException exc) {
      log.error("USER NOT FOUND ERROR: " + id);
      throw new ResponseStatusException(HttpStatus.valueOf(406), "The user does not exist.");
    } catch (NotAcceptableException exc) {
      log.error("DGGA ATTEMPTED TO REVOKE THEIR OWN ADMIN PERMISSIONS");
      throw new ResponseStatusException(HttpStatus.valueOf(409),
          "DGGA cannot revoke their own admin permissions");
    } catch (BadCredentialsException exc) {
      log.error("INSUFFICIENT PRIVILEGES: " + id);
      throw new ResponseStatusException(HttpStatus.valueOf(403), "Insufficient privileges");
    }
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
    public ResponseEntity<JSONObject> registerBusiness(@RequestBody Business business) {
      try {
        JSONObject response = businessService.saveBusiness(business);
        return new ResponseEntity<>(response, HttpStatus.valueOf(201));
      } catch (BusinessTypeException exc) {
        log.error("Error with supplied data");
        throw new ResponseStatusException(HttpStatus.valueOf(400), "Error with supplied data");
      } catch (Exception exc) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error.");
      }
    }

  /**
   * Search for a specific business using the id field.
   *
   * @param id unique identifier of the business being searched for
   * @return business entity matching the given id
   * @throws ResponseStatusException HTTP 401 Unauthorised & 406 Not Acceptable
   */
  @GetMapping("/businesses/{id}")
  public ResponseEntity<JSONObject> getBusinessById(@PathVariable long id) {
    try {
      log.info("GETTING BUSINESS BY ID: " + id);
      return new ResponseEntity<>(businessService.getBusinessById(id, true),
          HttpStatus.valueOf(200));
    } catch (BusinessNotFoundException exc) {
      log.error("BUSINESS NOT FOUND ERROR: " + id);
      throw new ResponseStatusException(HttpStatus.valueOf(406), "Business not found");
    } catch (Exception exc) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error.");
    }
  }
}
