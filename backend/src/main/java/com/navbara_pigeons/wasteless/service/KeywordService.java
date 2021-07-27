package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.entity.Keyword;
import java.util.List;

public interface KeywordService {

  List<Keyword> getKeywords(List<Long> keywordId);

  List<Keyword> getAllKeywords();

}
