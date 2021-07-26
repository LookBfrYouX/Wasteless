package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.dto.BasicUserDto;
import com.navbara_pigeons.wasteless.dto.CreateUserDto;
import com.navbara_pigeons.wasteless.dto.FullUserDto;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.enums.UserSortByOption;
import com.navbara_pigeons.wasteless.exception.AddressValidationException;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.exception.NotAcceptableException;
import com.navbara_pigeons.wasteless.exception.UserAlreadyExistsException;
import com.navbara_pigeons.wasteless.exception.UserAuthenticationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserRegistrationException;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import com.navbara_pigeons.wasteless.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.management.InvalidAttributeValueException;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Maximilian Birzer, Dawson Berry, Alec Fox
 */
@RestController
@Slf4j
@RequestMapping("")
@Tag(name = "User Endpoint", description = "The API endpoint for User related requests")
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
    return new ResponseEntity<>(response, HttpStatus.OK);
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
  @Operation(summary = "Register a new user", description = "New user registration")
  public ResponseEntity<JSONObject> registerUser(@RequestBody CreateUserDto user)
      throws UserNotFoundException, AddressValidationException, UserRegistrationException,
      UserAlreadyExistsException, UserAuthenticationException {
    JSONObject createdUserId = userService.saveUser(new User(user));
    log.info("ACCOUNT CREATED SUCCESSFULLY: " + user.getEmail());
    return new ResponseEntity<>(createdUserId, HttpStatus.CREATED);
  }

  /**
   * Search for a specific user using the id field.
   *
   * @param id unique identifier of the user being searched for
   * @return user entity matching the given id
   * @throws ResponseStatusException HTTP 401 Unauthorised & 406 Not Acceptable
   */
  @GetMapping("/users/{id}")
  @Operation(summary = "Get a user by their ID", description = "Authenticated users can get user information by their ID")
  public ResponseEntity<Object> getUserById(@PathVariable String id)
      throws UserNotFoundException {
    log.info("GETTING USER BY ID: " + id);
    User user = userService.getUserById(Long.parseLong(id));
    if (userService.isAdmin() || userService.isSelf(user.getEmail())) {
      return new ResponseEntity<>(new FullUserDto(user), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(new BasicUserDto(user), HttpStatus.OK);
    }
  }

  /**
   * Search for a user based of nickname or name(firstName, middleName, lastName)
   *
   * @param searchQuery   name being searched for
   * @param pagStartIndex The start index of the list to return, implemented for pagination, Can be
   *                      Null. This index is inclusive.
   * @param pagEndIndex   The stop index of the list to return, implemented for pagination, Can be
   *                      Null. This index is inclusive.
   * @param sortBy        Defines the field to be sorted, can be null.
   * @param isAscending   Boolean value, whether the sort order should be in ascending order. Is not
   *                      required and defaults to True.
   * @return List of all users matching the searchQuery
   * @throws ResponseStatusException Unknown Error
   */
  @GetMapping("/users/search")
  @Operation(summary = "Find Users by a search query", description = "Search with a search query to return a paginated/sorted list of Users")
  public ResponseEntity<Object> searchUsers(
      @Parameter(
          description =
              "Criteria to search users for (e.g: user’s full name or one or more of their"
                  + " names/nickname). Can include `AND` & `OR` for logical disjunction)"
      ) @RequestParam String searchQuery,
      @Parameter(
          description = "The start index of the list to return, implemented for pagination, Can be "
              + "Null. This index is inclusive."
      ) @RequestParam(required = false) Integer pagStartIndex,
      @Parameter(
          description = "The stop index of the list to return, implemented for pagination, Can be "
              + "Null. This index is inclusive."
      ) @RequestParam(required = false) Integer pagEndIndex,
      @Parameter(
          description = "Defines the field to be sorted, can be null."
      ) @RequestParam(required = false) UserSortByOption sortBy,
      @Parameter(
          description = "Boolean value, whether the sort order should be in ascending order. Is not"
              + " required and defaults to True."
      ) @RequestParam(required = false, defaultValue = "true") boolean isAscending)
      throws InvalidAttributeValueException, InvalidPaginationInputException {
    return new ResponseEntity<>(
        this.userService.searchUsers(searchQuery, pagStartIndex, pagEndIndex, sortBy, isAscending),
        HttpStatus.OK);
  }

  /**
   * Give the User matching the given 'id' parameter Global Application Admin (GAA) rights.
   *
   * @param id The unique identifier of the user being given GAA rights.
   */
  @PutMapping("/users/{id}/makeAdmin")
  @Operation(summary = "Make a user admin", description = "Allow admins to grant others admin permissions")
  public ResponseEntity<String> makeUserAdmin(@PathVariable String id)
      throws UserNotFoundException {
    userService.makeUserAdmin(Integer.parseInt(id));
    log.info("ADMIN PRIVILEGES GRANTED TO: " + id);
    return new ResponseEntity<>("Action completed successfully", HttpStatus.OK);
  }

  /**
   * This method removes the admin role from the user with the given ID. It can only be accessed if
   * the current user has the admin role.
   *
   * @param id The ID of the user who will lose the admin role.
   * @return HttpStatus 406 for Invalid ID format or User Doesn't Exist exception.
   */
  @PutMapping("/users/{id}/revokeAdmin")
  @Operation(summary = "Revoke a users admin permissions", description = "Admin users can use this endpoint to revoke admin permissions")
  public ResponseEntity<String> revokeAdminPermissions(@PathVariable long id)
      throws NotAcceptableException, UserNotFoundException {
    userService.revokeAdmin(id);
    log.info("ADMIN PRIVILEGES REVOKED FROM: " + id);
    return new ResponseEntity<>("Action completed successfully", HttpStatus.OK);
  }
}
