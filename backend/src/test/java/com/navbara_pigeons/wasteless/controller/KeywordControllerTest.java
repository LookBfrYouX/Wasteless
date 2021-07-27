package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.testprovider.ControllerTestProvider;
import lombok.With;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class KeywordControllerTest extends ControllerTestProvider {

    @Test
    @WithUserDetails(value = "mbi47@uclive.ac.nz")
    void getAllKeywords_expectOk() throws Exception {
        String endpointUrl = "/keywords";
        mockMvc.perform(get(endpointUrl)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "mbi47@uclive.ac.nz")
    void getAllKeywords_expectValidKeywords() throws Exception {
        String endpointUrl = "/keywords";
        MvcResult results = mockMvc.perform(get(endpointUrl)).andReturn();
        Assertions.assertTrue(results.getResponse().getContentAsString().contains("Dairy"));
    }

    @Test
    @WithUserDetails(value = "mbi47@uclive.ac.nz")
    void getAllKeywords_checkInvalidKeyword() throws Exception {
        String endpointUrl = "/keywords";
        MvcResult results = mockMvc.perform(get(endpointUrl)).andReturn();
        Assertions.assertFalse(results.getResponse().getContentAsString().contains("Jibberjabber"));
    }

}
