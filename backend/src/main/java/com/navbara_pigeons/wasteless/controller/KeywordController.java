package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.service.KeywordService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * This controller class contains endpoints that deal with keywords.
 */
@RestController
@Slf4j
public class KeywordController {

  private final KeywordService keywordService;

  public KeywordController(@Autowired KeywordService keywordService) {
    this.keywordService = keywordService;
  }

  /**
   * This endpoint returns a list of all keywords.
   *
   * @return ResponseEntity
   */
  @GetMapping("/keywords")
  @Operation(summary = "Show a list of keywords", description = "Return a list of all keywords")
  public ResponseEntity<Object> getAllKeywords() {
    log.info("RETRIEVED LIST OF KEYWORDS");
    return new ResponseEntity<>(keywordService.getAllKeywords(), HttpStatus.OK);
  }

}