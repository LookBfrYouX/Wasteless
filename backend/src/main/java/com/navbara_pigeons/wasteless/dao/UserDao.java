package com.navbara_pigeons.wasteless.dao;


import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import java.util.List;
import javax.management.InvalidAttributeValueException;
import org.springframework.data.util.Pair;

public interface UserDao {

  void saveUser(User user);

  void deleteUser(User user);

  User getUserById(long id) throws UserNotFoundException;

  User getUserByEmail(String email) throws UserNotFoundException;

  boolean userExists(String email);

  List<User> searchUsers(String searchQuery)
      throws InvalidAttributeValueException, InvalidPaginationInputException;

  Pair<List<User>, Long> searchUsers(String searchQuery, PaginationBuilder pagBuilder)
      throws InvalidAttributeValueException;

}
