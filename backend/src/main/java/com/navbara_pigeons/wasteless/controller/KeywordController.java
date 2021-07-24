package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.dto.CreateKeywordDto;
import com.navbara_pigeons.wasteless.entity.Keyword;
import com.navbara_pigeons.wasteless.service.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class KeywordController {

    private final KeywordService keywordService;

    public KeywordController(@Autowired KeywordService keywordService) {
        this.keywordService = keywordService;
    }

    @GetMapping("/keywords")
    public ResponseEntity getKeywords(@RequestParam List<Long> ids) {
        System.out.println(ids);
        return new ResponseEntity(keywordService.getKeywords(ids), HttpStatus.valueOf(200));
    }

}
