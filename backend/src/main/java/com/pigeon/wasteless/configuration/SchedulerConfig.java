package com.pigeon.wasteless.configuration;

import com.pigeon.wasteless.dao.UserDao;
import com.pigeon.wasteless.entity.User;
import com.pigeon.wasteless.exception.UserNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Slf4j
@EnableScheduling
public class SchedulerConfig {

  // ATTENTION: This class talks directly to the DAO or DATABASE. EDIT WITH CAUTION!!!

  private final UserDao userDao;
  @Value("${dgaa.user.email}")
  private String dgaaEmail;
  @Value("${dgaa.user.password}")
  private String dgaaPassword;
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  public SchedulerConfig(UserDao userDao, BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.userDao = userDao;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  // TODO: have delay using an environment variable
  // TODO: add testing

  /**
   * Scheduler method, runs every timeDelay ms
   */
  @Scheduled(fixedRateString = "${dgaa.scheduler.interval}", initialDelay = 1000)
  @Transactional
  public void testScheduler() {
    try {
      User dgaa = userDao.getUserByEmail(this.dgaaEmail);
      if (!dgaa.getRole().contains("ADMIN")) {
        dgaa.makeAdmin();
        userDao.saveUser(dgaa);
        log.info("RE-GRANTED ADMIN TO DGAA USER");
      }
    } catch (UserNotFoundException err) {
      insertDgaa();
    }
  }

  /**
   * Creating and saving new DGAA
   */
  public void insertDgaa() {
    User dgaa = new User();
    dgaa.setFirstName("Admin")
        .setLastName("Admin")
        .setEmail(this.dgaaEmail)
        .setDateOfBirth(LocalDate.parse("2000-01-01").format(DateTimeFormatter.ISO_LOCAL_DATE))
        .setHomeAddress("20 Kirkwood Avenue, Upper Riccarton, Christchurch 8041")
        .setCreated(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
        .setRole("ROLE_ADMIN")
        .setPassword(bCryptPasswordEncoder.encode(this.dgaaPassword));
    userDao.saveUser(dgaa);
    log.info("RE-INSERTED DGAA USER");
  }

}
