package com.pigeon.wasteless.dao;

import com.pigeon.wasteless.entity.User;
import com.pigeon.wasteless.exception.UserNotFoundException;
import java.util.List;

public interface UserDao {

  void saveUser(User user);

  void deleteUser(User user);

  User getUserById(long id) throws UserNotFoundException;

  User getUserByEmail(String email) throws UserNotFoundException;

  boolean userExists(String email);

  List<User> exactSearchUsers(String searchQuery);

  List<User> partialSearchUsers(String searchQuery);

}
