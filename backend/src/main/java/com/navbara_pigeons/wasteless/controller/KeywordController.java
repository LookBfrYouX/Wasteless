package com.navbara_pigeons.wasteless.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KeywordController {

    private final KeywordService keywordService;

    public KeywordController(@Autowired KeywordService keywordService) {
        this.keywordService = keywordService;
    }

    @GetMapping('/keywords/{id}')
    public ResponseEntity getKeywords(@PathVariable Long id) {

    }

}
