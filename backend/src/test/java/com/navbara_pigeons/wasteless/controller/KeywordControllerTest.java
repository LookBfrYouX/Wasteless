package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.testprovider.ControllerTestProvider;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class KeywordControllerTest extends ControllerTestProvider {

    @Test
    @WithUserDetails(value = "mbi47@uclive.ac.nz")
    void getAllKeywords_expectOk() throws Exception {
        String endpointUrl = "/keywords";
        mockMvc.perform(get(endpointUrl)).andExpect(status().isOk());
    }

}
