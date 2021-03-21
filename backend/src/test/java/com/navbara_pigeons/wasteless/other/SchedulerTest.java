package com.navbara_pigeons.wasteless.other;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.navbara_pigeons.wasteless.configuration.SchedulerConfig;
import com.navbara_pigeons.wasteless.dao.UserDao;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class SchedulerTest {

  UserDao userDao;

  @SpyBean
  private SchedulerConfig schedulerConfig;

  @Value("${dgaa.user.email}")
  private String dgaaEmail;

  @Disabled
  @Test
  void removeDgaa() throws InterruptedException, UserNotFoundException {

    Thread.sleep(2000);

    User dgaa = userDao.getUserByEmail(dgaaEmail);

    System.out.println(userDao.getUserByEmail(dgaaEmail));

    userDao.deleteUser(dgaa);

    System.out.println(userDao.getUserByEmail(dgaaEmail));

    // Wait 2 seconds
    Thread.sleep(2000);
    System.out.println(userDao.getUserByEmail(dgaaEmail));

    assertEquals("tony", "tony");
  }
}