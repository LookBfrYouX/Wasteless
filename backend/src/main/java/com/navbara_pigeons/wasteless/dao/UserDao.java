package com.navbara_pigeons.wasteless.dao;


import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;

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
