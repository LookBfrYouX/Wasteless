package com.navbara_pigeons.wasteless.configuration;

import com.navbara_pigeons.wasteless.dao.AddressDao;
import com.navbara_pigeons.wasteless.dao.UserDao;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.navbara_pigeons.wasteless.service.CountryDataFetcherService;
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
  private final AddressDao addressDao;
  private final CountryDataFetcherService countryDataFetcherService;

  @Value("${dgaa.user.email}")
  private String dgaaEmail;
  @Value("${dgaa.user.password}")
  private String dgaaPassword;

  /**
   * Oldest the country info file can be before it must be re-downloaded
   */
  @Value("${country_data.max_age_days}")
  private float maxAgeDays;

  /**
   * Number of times it can try re-download/re-parse the country info file in a single scheduler interval
   */
  @Value("${country_data.num_retries}")
  private int numRetries;

  /**
   * How often it should check the age of the file. Note that if downloading/parsing fails in one session, the country
   * or currency will be unusable until at least the next scheduler session
   */
  @Value("${country_data.scheduler.interval}")
  private int countryDataSchedulerInterval;

  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  public SchedulerConfig(UserDao userDao, AddressDao addressDao,
                         CountryDataFetcherService countryDataFetcherService,
                         BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.userDao = userDao;
    this.addressDao = addressDao;
    this.countryDataFetcherService = countryDataFetcherService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

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
        log.info("Granted Admin role to DGAA user");
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
    Address address = new Address()
        .setStreetNumber("20")
        .setStreetName("Kirkwood Avenue")
        .setPostcode("8041")
        .setCity("Christchurch")
        .setRegion("Canterbury")
        .setCountry("New Zealand");

    addressDao.saveAddress(address);
    dgaa.setFirstName("Admin")
        .setLastName("Admin")
        .setEmail(this.dgaaEmail)
        .setDateOfBirth(LocalDate.parse("2000-01-01").format(DateTimeFormatter.ISO_LOCAL_DATE))
        .setHomeAddress(address)
        .setCreated(ZonedDateTime.now(ZoneOffset.UTC))
        .setRole("ROLE_ADMIN")
        .setPassword(bCryptPasswordEncoder.encode(this.dgaaPassword));
    userDao.saveUser(dgaa);
    log.info("Inserted DGAA user");
  }

}
