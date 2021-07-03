package com.navbara_pigeons.wasteless.dao;


import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import java.util.List;
import javax.management.InvalidAttributeValueException;

public interface UserDao {

  void saveUser(User user);

  void deleteUser(User user);

  User getUserById(long id) throws UserNotFoundException;

  User getUserByEmail(String email) throws UserNotFoundException;

  boolean userExists(String email);

  List<User> searchUsers(String searchQuery) throws InvalidAttributeValueException;

  List<User> searchUsers(String searchQuery, Integer pagStartIndex, Integer pagEndIndex)
      throws InvalidAttributeValueException;

}
