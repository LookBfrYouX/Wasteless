package com.navbara_pigeons.wasteless.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navbara_pigeons.wasteless.dao.UserDao;
import com.navbara_pigeons.wasteless.dto.BasicUserDto;
import com.navbara_pigeons.wasteless.dto.FullUserDto;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.*;
import com.navbara_pigeons.wasteless.security.model.BasicUserDetails;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import com.navbara_pigeons.wasteless.validation.UserServiceValidation;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.management.InvalidAttributeValueException;
import javax.transaction.Transactional;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * A UserService implementation.
 */
@Service
public class UserServiceImpl implements UserService {

  private final UserDao userDao;
  private final AddressService addressService;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final BCryptPasswordEncoder encoder;
  private final ObjectMapper objectMapper;
  private final BusinessService businessService;

  @Value("${dgaa.user.email}")
  private String dgaaEmail;

  /**
   * UserServiceImplementation constructor that takes autowired parameters and sets up the service
   * for interacting with all user related services.
   *
   * @param userDao                      The UserDataAccessObject.
   * @param addressService             The address service
   * @param authenticationManagerBuilder The global AuthenticationManagerBuilder.
   * @param encoder                      Password encoder.
   * @param businessService
   */
  @Autowired
  public UserServiceImpl(UserDao userDao, AddressService addressService,
      AuthenticationManagerBuilder authenticationManagerBuilder, BCryptPasswordEncoder encoder,
      ObjectMapper objectMapper, BusinessService businessService) {
    this.userDao = userDao;
    this.addressService = addressService;
    this.authenticationManagerBuilder = authenticationManagerBuilder;
    this.encoder = encoder;
    this.objectMapper = objectMapper;
    this.businessService = businessService;
  }

  /**
   * Performs basic business checks, sets role, created date and hashes password before sending to
   * the dao
   *
   * @param user User object to be saved.
   * @return login response
   * @throws UserAlreadyExistsException Thrown when a user already exists in the database
   * @throws UserRegistrationException  Thrown when invalid fields received
   */
  @Override
  @Transactional
  public JSONObject saveUser(User user)
          throws UserAlreadyExistsException, UserRegistrationException, UserNotFoundException, AddressValidationException {
    // Email validation
    if (!UserServiceValidation.requiredFieldsNotEmpty(user)) {
      throw new UserRegistrationException("Required user fields cannot be null");
    }
    if (!UserServiceValidation.isEmailValid(user.getEmail())) {
      throw new UserRegistrationException("Invalid email");
    }
    if (userDao.userExists(user.getEmail())) {
      throw new UserAlreadyExistsException("User already exists");
    }
    // Validates the users DOB
    if (!UserServiceValidation.isDobValid(user.getDateOfBirth())) {
      throw new UserRegistrationException("Invalid date of birth");
    }
    // Check user is over 13 years old
    if (!LocalDate.parse(user.getDateOfBirth()).isBefore(LocalDate.now().minusYears(13))) {
      throw new UserRegistrationException("Must be 13 years or older to register");
    }
    user.setDateOfBirth(
        LocalDate.parse(user.getDateOfBirth()).format(DateTimeFormatter.ISO_LOCAL_DATE));

    // Password and field validation
    if (!UserServiceValidation.isPasswordValid(user.getPassword())) {
      throw new UserRegistrationException("Password does not pass validation check");
    }

    // Set user credentials for logging in after registering
    UserCredentials userCredentials = new UserCredentials();
    userCredentials.setEmail(user.getEmail());
    userCredentials.setPassword(user.getPassword());
    // Set created date, hash password
    user.setCreated(ZonedDateTime.now(ZoneOffset.UTC));
    user.setPassword(encoder.encode(user.getPassword()));
    user.setRole("ROLE_USER");

    this.addressService.saveAddress(user.getHomeAddress());
    this.userDao.saveUser(user);

    // Logging in the user and returning the id
    JSONObject response = login(userCredentials);
    return response;
  }

  /**
   * Calls the userDao to get the specified user If not an admin or retrieving details for another
   * user, the user email address, date of birth, phone number and home address street
   * number/name/post code are not returned
   *
   * @param id the id of the user
   * @return the User DTO instance of the user
   */
  @Override
  @Transactional
  public Object getUserById(long id) throws UserNotFoundException {
    User user = userDao.getUserById(id);
    if (isAdmin() || isSelf(user.getEmail())) {
      return new FullUserDto(user);
    } else {
      return new BasicUserDto(user);
    }
  }

  /**
   * Login business logic. This method gets UserCredentials from the endpoint and attempts to
   * authenticate the user. Authentication is done through the global AuthenticationManagerBuilder.
   *
   * @param userCredentials The UserCredentials object storing the email and password.
   * @return JSON response with user ID
   * @throws AuthenticationException An authentication exception that is assigned HTTP error codes
   *                                 at the controller.
   */
  @Override
  public JSONObject login(UserCredentials userCredentials)
      throws AuthenticationException, UserNotFoundException {
    // Check for null in userCredentials
    if (userCredentials.getEmail() == null || userCredentials.getPassword() == null) {
      throw new BadCredentialsException("No username/password supplied.");
    }
    // Create authentication token
    UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(
        userCredentials.getEmail(),
        userCredentials.getPassword()
    );
    // Perform authentication and get authentication object
    Authentication auth = authenticationManagerBuilder.getOrBuild().authenticate(authReq);
    // Set current security context with authentication
    SecurityContext securityContext = SecurityContextHolder.getContext();
    securityContext.setAuthentication(auth);
    // Build and return JSON response
    JSONObject response = new JSONObject();
    User user = userDao.getUserByEmail(userCredentials.getEmail());
    response.put("userId", user.getId());
    return response;
  }

  /**
   * This method searches for a user using a given email address.
   *
   * @param email A unique email address
   * @return Returns the user with the corresponding email address.
   */
  @Override
  public User getUserByEmail(String email) throws UserNotFoundException {
    return userDao.getUserByEmail(email);
  }


  /**
   * Calls the userDao to search for users using the given username
   *
   * @param searchQuery the name being searched for
   * @return A list containing all the users whose names/nickname match the username
   */
  @Override
  @Transactional
  public List<BasicUserDto> searchUsers(String searchQuery) throws InvalidAttributeValueException {
    List<BasicUserDto> results = new ArrayList<>();
    for (User user : userDao.searchUsers(searchQuery)) {
      results.add(new BasicUserDto(user));
    }
    return results;
  }

  /**
   * This method gives administrator privileges to another user if authorised.
   *
   * @param id the id of the User to be made Admin.
   * @throws UserNotFoundException   The user to be made admin was not found.
   * @throws BadCredentialsException The requester is not an Admin.
   */
  @Override
  @Transactional
  public void makeUserAdmin(long id) throws UserNotFoundException, BadCredentialsException {
    // Check if the requester have sufficient privileges
    if (!isAdmin()) {
      throw new BadCredentialsException("Insufficient privileges");
    }
    // Get the user to me made admin
    User userToBeAdmin = userDao.getUserById(id);
    // Add GAA role to user
    userToBeAdmin.makeAdmin();
    userDao.saveUser(userToBeAdmin);
  }

  /**
   * This method revokes the admin privileges of the user with the given id. If that user is
   * currently logged in, it resets their authentication to reflect the new roles.
   *
   * @param id The ID of the user who will lose admin privileges.
   * @throws UserNotFoundException The exception thrown if the user is not in the database.
   */
  @Override
  @Transactional
  public void revokeAdmin(long id) throws UserNotFoundException, NotAcceptableException {
    // Test if current user is admin
    if (!isAdmin()) {
      throw new BadCredentialsException("Insufficient privileges");
    }
    // Get the user
    User user = userDao.getUserById(id);

    if (user.getEmail().equals(this.dgaaEmail)) {
      throw new NotAcceptableException("DGAA cannot revoke their own admin credentials");
    }

    // Remove the admin role if it is present
    user.revokeAdmin();
    // Update the database with the updated user
    userDao.saveUser(user);
    // Check if the revoked user is the current user
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth.getName().equalsIgnoreCase(user.getEmail())) {
      // Update the current security context with the updated authorizations
      UserDetails userDetails = new BasicUserDetails(user);
      Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(),
          auth.getCredentials(), userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
  }


  /**
   * This helper method tests whether the current user has the ADMIN role
   *
   * @return true if user is admin, false if not admin or not authenticated
   */
  @Override
  public boolean isAdmin() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null) {
      return false;
    }
    for (GrantedAuthority simpleGrantedAuthority : authentication.getAuthorities()) {
      if (simpleGrantedAuthority.getAuthority().contains("ADMIN")) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if user with the given email is the user making this request
   *
   * @param userEmail User to check against
   * @return true if logged in user is the referenced user
   */
  @Override
  public boolean isSelf(String userEmail) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (userEmail.equals(auth.getName())) {
      return true;
    }
    return false;
  }

  /**
   * Gets currently logged in user email
   * @return user email, or null if not authenticated
   */
  @Override
  public String getLoggedInUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return ((UserDetails) authentication.getPrincipal()).getUsername();
  }

  /**
   * Gets currently logged in user
   * @return logged in user
   * @throws UserNotFoundException
   */
  @Override
  public User getLoggedInUser() throws UserNotFoundException {
    return userDao.getUserByEmail(getLoggedInUserEmail());
  }

}
