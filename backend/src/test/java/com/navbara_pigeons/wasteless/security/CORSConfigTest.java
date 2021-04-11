package com.navbara_pigeons.wasteless.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
class CORSConfigTest {

  @Autowired
  public MockMvc mockMvc;
  @Autowired
  private WebApplicationContext wac;

  @Before
  public void setup() {
    DefaultMockMvcBuilder builder = MockMvcBuilders
        .webAppContextSetup(this.wac)
        .apply(SecurityMockMvcConfigurers.springSecurity())
        .dispatchOptions(true);
    this.mockMvc = builder.build();

  }

  @Test
  public void testCorsValidLocalhostOrigin() throws Exception {
    String validOrigin = "http://localhost:9500";
    this.mockMvc
        .perform(options("/login")
            .header("Access-Control-Request-Method", "POST")
            .header("Origin", validOrigin))
        .andExpect(status().isOk());
  }

  @Test
  public void testCorsValidCanterburyOrigin() throws Exception {
    String validOrigin = "https://csse-s302g3.canterbury.ac.nz";
    this.mockMvc
        .perform(options("/login")
            .header("Access-Control-Request-Method", "POST")
            .header("Origin", validOrigin))
        .andExpect(status().isOk());
  }

  @Test
  public void testCorsInvalidOrigin() throws Exception {
    String invalidOrigin = "http://www.notavalidorigin.com";
    this.mockMvc
        .perform(options("/login")
            .header("Access-Control-Request-Method", "POST")
            .header("Origin", invalidOrigin))
        .andExpect(status().isForbidden());
  }
}