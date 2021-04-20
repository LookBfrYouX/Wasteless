package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.service.CountryDataFetcherService;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@Slf4j
public class MiscController {
    private final CountryDataFetcherService countryDataFetcherService;

    public MiscController(@Autowired CountryDataFetcherService countryDataFetcherService) {
        this.countryDataFetcherService = countryDataFetcherService;
    }

    @GetMapping("/misc/countryCodes")
    public ResponseEntity<JSONObject> countryCodes() {
        try {
            return new ResponseEntity<>(countryDataFetcherService.getTwoDigitCountryCodeDictionary(), HttpStatus.valueOf(200));
        } catch (IllegalStateException e) {
            log.warn("Could not send country data", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Country data not available at the moment");
        }

    }
}
