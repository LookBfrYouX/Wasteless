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

  @Scheduled(fixedRateString = "${country_data.scheduler.interval}", initialDelayString = "${country_data.scheduler.initial_delay}")
  public void countryDataScheduler() {
    countryDataScheduler(0);
  }

  /**
   * Checks if a file was last modified more than *n* milliseconds from the current time
   * @param path path to file
   * @param maxAgeMs max age of the file
   * @return true if delta between current time and last modification time is greater than *n*
   * @throws IOException
   */
  private boolean fileOlderThan(Path path, long maxAgeMs) throws IOException {
    BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);

    return (attributes.lastModifiedTime().toMillis() + maxAgeMs < System.currentTimeMillis());
  }

  /**
   * Scheduler for country/currency info
   * Downloads the JSON if it does not exist or is older than max_age_days and then parses it.
   * If the parsing fails the file is deleted and download is re-attempted
   * If this fails more than num_retries times it gives up until the scheduler re-runs
   * @param count number of times it has run in this scheduler cycle
   */
  public void countryDataScheduler(int count) {
    if (count >= numRetries) {
      log.error(String.format("Country data download failed %d times. Will re-download in %d ms", numRetries, countryDataSchedulerInterval));
      return;
    }

    Path path = countryDataFetcherService.getDefaultJsonPath();
    long maxDeltaMilliseconds = (long) (maxAgeDays * 24 * 60 * 60 * 1000); // Don't download if it is less than n days old

    boolean download = false; // if downloading, always reload
    boolean loadOnly = false; // use existing file and don't download

    try {
      if (path.toFile().exists()) {
        if (fileOlderThan(path, maxDeltaMilliseconds)) {
          log.info(String.format("Country data file older than %f days; re-downloading", maxAgeDays));
          download = true;
        }
      } else {
          log.info("Country data file does not exist; downloading");
          download = true;
        }
    } catch(IOException e) {
      log.info("Error getting age of file", e);
      download = true; // If exception occurs just try and re-download it
    }

    if (!download && !countryDataFetcherService.dataLoaded()) {
      // File exists and is up to date, and country data hasn't been loaded (first time it has run)
      loadOnly = true;
    }

    if (!loadOnly && download) {
      try {
        countryDataFetcherService.callAndSaveApiResponse();
        log.info("Currency data downloaded");
      } catch(Exception e) {
        log.warn("Error downloading and saving country data. Retrying...", e);
        countryDataScheduler(count + 1);
        return;
      }
    }

    if (loadOnly || download) {
      if (loadOnly) {
        log.info("Using existing country data file");
      }
      try {
        countryDataFetcherService.reloadCountryDataFromDisk();
        log.info("Currency data loaded");
      } catch (IOException e) {
        log.warn("Error loading country data from disk", e);
        if (path.toFile().exists()) {
          log.info("Deleting country data and retrying ...");
          path.toFile().delete();
          countryDataScheduler(count + 1);
        }
      }
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
