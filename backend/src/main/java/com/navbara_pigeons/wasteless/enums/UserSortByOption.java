package com.navbara_pigeons.wasteless.enums;

import com.navbara_pigeons.wasteless.entity.User;

public enum UserSortByOption implements SortByOption {
  firstName,
  middleName,
  lastName,
  nickname;

  @Override
  public Class<?> getEntity() {
    return User.class;
  }
}
