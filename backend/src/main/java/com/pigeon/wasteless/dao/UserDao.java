package com.pigeon.wasteless.dao;

import com.pigeon.wasteless.entity.User;
import com.pigeon.wasteless.exception.UserNotFoundException;

import javax.management.InvalidAttributeValueException;
import java.util.List;

public interface UserDao {

  void saveUser(User user);

  void deleteUser(User user);

  User getUserById(long id) throws UserNotFoundException;

  User getUserByEmail(String email) throws UserNotFoundException;

  boolean userExists(String email);

  List<User> searchUsers(String searchQuery) throws InvalidAttributeValueException;

}
