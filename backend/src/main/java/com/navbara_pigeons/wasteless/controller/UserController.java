package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.dto.BasicUserDto;
import com.navbara_pigeons.wasteless.dto.CreateUserDto;
import com.navbara_pigeons.wasteless.dto.FullUserDto;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.AddressValidationException;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.exception.NotAcceptableException;
import com.navbara_pigeons.wasteless.exception.UserAlreadyExistsException;
import com.navbara_pigeons.wasteless.exception.UserAuthenticationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserRegistrationException;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import com.navbara_pigeons.wasteless.service.UserService;
import javax.management.InvalidAttributeValueException;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  public UserController(@Autowired UserService userService) {
    this.userService = userService;
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
  public ResponseEntity<JSONObject> login(@RequestBody UserCredentials userCredentials)
      throws UserNotFoundException, AuthenticationException, UserAuthenticationException {
    // Attempt to login and return JSON userId if successful
    JSONObject response = userService.login(userCredentials);
    log.info("SUCCESSFUL LOGIN: " + userCredentials.getEmail());
    return new ResponseEntity<>(response, HttpStatus.valueOf(200));
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
  public ResponseEntity<JSONObject> registerUser(@RequestBody CreateUserDto user)
      throws UserNotFoundException, AddressValidationException, UserRegistrationException, UserAlreadyExistsException, UserAuthenticationException {
    JSONObject createdUserId = userService.saveUser(new User(user));
    log.info("ACCOUNT CREATED SUCCESSFULLY: " + user.getEmail());
    return new ResponseEntity<>(createdUserId, HttpStatus.valueOf(201));
  }

  /**
   * Search for a specific user using the id field.
   *
   * @param id unique identifier of the user being searched for
   * @return user entity matching the given id
   * @throws ResponseStatusException HTTP 401 Unauthorised & 406 Not Acceptable
   */
  @GetMapping("/users/{id}")
  public ResponseEntity<Object> getUserById(@PathVariable String id)
      throws UserNotFoundException {
    log.info("GETTING USER BY ID: " + id);
    User user = userService.getUserById(Long.parseLong(id));
    if (userService.isAdmin() || userService.isSelf(user.getEmail())) {
      return new ResponseEntity<>(new FullUserDto(user), HttpStatus.valueOf(200));
    } else {
      return new ResponseEntity<>(new BasicUserDto(user), HttpStatus.valueOf(200));
    }
  }

  /**
   * Search for a user based of nickname or name(firstName, middleName, lastName)
   *
   * @param searchQuery   name being searched for
   * @param pagStartIndex The start index of the list to return, implemented for pagination, Can be Null
   * @param pagEndIndex   The stop index of the list to return, implemented for pagination, Can be Null
   * @param sortBy        Defines any user sorting needed and the direction (ascending or
   *                      descending). In the format "fieldName-<acs/desc>", Can be Null
   * @return List of all users matching the searchQuery
   * @throws ResponseStatusException Unknown Error
   */
  @GetMapping("/users/search")
  public ResponseEntity<Object> searchUsers(@RequestParam String searchQuery,
      @RequestParam(required = false) Integer pagStartIndex,
      @RequestParam(required = false) Integer pagEndIndex,
      @RequestParam(required = false) String sortBy)
      throws InvalidAttributeValueException, InvalidPaginationInputException {
    return new ResponseEntity<>(
        this.userService.searchUsers(searchQuery, pagStartIndex, pagEndIndex, sortBy),
        HttpStatus.valueOf(200));
  }

  /**
   * Give the User matching the given 'id' parameter Global Application Admin (GAA) rights.
   *
   * @param id The unique identifier of the user being given GAA rights.
   */
  @PutMapping("/users/{id}/makeAdmin")
  public ResponseEntity<String> makeUserAdmin(@PathVariable String id)
      throws UserNotFoundException {
    userService.makeUserAdmin(Integer.parseInt(id));
    log.info("ADMIN PRIVILEGES GRANTED TO: " + id);
    return new ResponseEntity<>("Action completed successfully", HttpStatus.valueOf(200));
  }

  /**
   * This method removes the admin role from the user with the given ID. It can only be accessed if
   * the current user has the admin role.
   *
   * @param id The ID of the user who will lose the admin role.
   * @return HttpStatus 406 for Invalid ID format or User Doesn't Exist exception.
   */
  @PutMapping("/users/{id}/revokeAdmin")
  public ResponseEntity<String> revokeAdminPermissions(@PathVariable long id)
      throws NotAcceptableException, UserNotFoundException {
    userService.revokeAdmin(id);
    log.info("ADMIN PRIVILEGES REVOKED FROM: " + id);
    return new ResponseEntity<>("Action completed successfully", HttpStatus.valueOf(200));
  }
}
