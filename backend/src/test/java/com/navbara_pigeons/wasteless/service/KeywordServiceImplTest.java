package com.navbara_pigeons.wasteless.service;

import static org.mockito.Mockito.when;
import com.navbara_pigeons.wasteless.dao.KeywordDao;
import com.navbara_pigeons.wasteless.entity.Keyword;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.mockito.Mock;

public class KeywordServiceImplTest extends ServiceTestProvider {

  @Mock
  KeywordDao keywordDao;

  @Test
  public void getKeyword_validIds_expectValidKeywords() {
    long[] keywordIdsArray = { 1 };
    List<Long> keywordIds = Arrays.stream(keywordIdsArray).boxed().collect(Collectors.toList());
    Keyword keyword1 = new Keyword();
    keyword1.setName("Dairy");
    ArrayList<Keyword> expectedKeywords = new ArrayList<>();
    expectedKeywords.add(keyword1);
    ArrayList<Keyword> returnedKeywords = new ArrayList<>();
    when(keywordDao.findAllById(keywordIds)).thenReturn(returnedKeywords);
    assertKeywordEquals(returnedKeywords, expectedKeywords);
  }
}
