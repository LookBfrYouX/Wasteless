package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.*;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import java.util.List;
import javax.management.InvalidAttributeValueException;
import net.minidev.json.JSONObject;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

public interface UserService {

  JSONObject saveUser(User user)
          throws UserAlreadyExistsException, UserRegistrationException, UserNotFoundException, AddressValidationException;

  JSONObject login(UserCredentials userCredentials)
      throws AuthenticationException, UserNotFoundException;

  User getUserById(long id) throws UserNotFoundException, UnhandledException;

  User getUserByEmail(String email) throws UserNotFoundException;

  void revokeAdmin(long id) throws UserNotFoundException, NotAcceptableException;

  List<User> getAllUsers();

  List<User> searchUsers(String searchQuery) throws InvalidAttributeValueException;

  void makeUserAdmin(long id) throws UserNotFoundException, BadCredentialsException;

  boolean isAdmin();

  boolean isSelf(long id) throws UserNotFoundException, UnhandledException;

}
