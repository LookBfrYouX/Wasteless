package com.navbara_pigeons.wasteless.service;

import static org.mockito.Mockito.when;
import com.navbara_pigeons.wasteless.dao.KeywordDao;
import com.navbara_pigeons.wasteless.entity.Keyword;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class KeywordServiceImplTest extends ServiceTestProvider {

  @Mock
  KeywordDao keywordDao;

  @Test
  void getKeyword_validIds_expectValidKeywords() {
    long[] keywordIdsArray = {1};
    List<Long> keywordIds = Arrays.stream(keywordIdsArray).boxed().collect(Collectors.toList());
    Keyword keyword1 = new Keyword();
    keyword1.setName("Dairy");
    ArrayList<Keyword> expectedKeywords = new ArrayList<>();
    expectedKeywords.add(keyword1);
    ArrayList<Keyword> returnedKeywords = new ArrayList<>();
    when(keywordDao.findAllById(keywordIds)).thenReturn(returnedKeywords);
    assertKeywordEquals(returnedKeywords, expectedKeywords);
  }

  @Test
  void getKeyword_validIds_expectEmptyKeywords() {
    long[] keywordIdsArray = {2};
    List<Long> keywordIds = Arrays.stream(keywordIdsArray).boxed().collect(Collectors.toList());
    ArrayList<Keyword> expectedKeywords = new ArrayList<>();
    ArrayList<Keyword> returnedKeywords = new ArrayList<>();
    when(keywordDao.findAllById(keywordIds)).thenReturn(returnedKeywords);
    assertKeywordEquals(returnedKeywords, expectedKeywords);
  }

  @Test
  void getAllKeywords_expectAllReturned() {
    ArrayList<Keyword> returnedKeywords = new ArrayList<>();
    Keyword keyword1 = new Keyword();
    keyword1.setName("test1");
    Keyword keyword2 = new Keyword();
    keyword2.setName("test2");
    returnedKeywords.add(keyword1);
    returnedKeywords.add(keyword2);
    when(keywordDao.findAll()).thenReturn(returnedKeywords);
    Assertions.assertEquals(keywordDao.findAll(), returnedKeywords);
  }
}
