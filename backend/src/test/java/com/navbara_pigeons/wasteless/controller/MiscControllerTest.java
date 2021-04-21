package com.navbara_pigeons.wasteless.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.navbara_pigeons.wasteless.service.CountryDataFetcherService;
import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MiscControllerTest extends MainTestProvider {

    @Autowired
    public MockMvc mockMvc;

    final String endpointUrl = "/misc/countryData";

    @Test
    void countryDataNoData() throws Exception {
        this.countryDataFetcherService.resetCountryData();
        this.mockMvc.perform(get(endpointUrl)).andExpect(status().is5xxServerError());
    }

    @Test
    void countryDataStandard() throws Exception {
        this.loadDefaultCountryDataIfNotLoaded();
        this.mockMvc.perform(get(endpointUrl)).andExpect(status().isOk());
    }
}
