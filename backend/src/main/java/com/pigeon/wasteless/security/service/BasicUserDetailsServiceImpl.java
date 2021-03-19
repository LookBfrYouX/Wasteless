package com.pigeon.wasteless.security.service;

import com.pigeon.wasteless.dao.UserDao;
import com.pigeon.wasteless.entity.User;
import com.pigeon.wasteless.exception.UserNotFoundException;
import com.pigeon.wasteless.security.model.BasicUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BasicUserDetailsServiceImpl implements UserDetailsService, BasicUserDetailsService {

  private final UserDao userDao;

  @Autowired
  public BasicUserDetailsServiceImpl(UserDao userDao) {
    this.userDao = userDao;
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
    User user = null;
    try {
      user = userDao.getUserByEmail(userName);
    } catch (UserNotFoundException e) {
      e.printStackTrace();
    }
    if (user == null) {
      throw new UsernameNotFoundException("User not found: " + userName);
    }
    return new BasicUserDetails(user);
  }

  @Override
  @Transactional
  public void saveUser(User user) {
    this.userDao.saveUser(user);
  }

}
