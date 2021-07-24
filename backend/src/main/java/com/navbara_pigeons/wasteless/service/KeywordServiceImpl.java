package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.KeywordDao;
import com.navbara_pigeons.wasteless.entity.Keyword;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeywordServiceImpl implements KeywordService {

  private final KeywordDao keywordDao;

  @Autowired
  public KeywordServiceImpl(KeywordDao keywordDao) {
    this.keywordDao = keywordDao;
  }

  /**
   * Retrieves keywords given a list of keywordIds.
   * @param keywordIds
   * @return
   */
  @Override
  public List<Keyword> getKeywords(List<Long> keywordIds) {
    List<Keyword> keywords = new ArrayList<>();
    keywordDao.findAllById(keywordIds).forEach(keywords::add);
    return keywords;
  }
}
