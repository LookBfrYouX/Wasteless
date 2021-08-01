package com.navbara_pigeons.wasteless.dto;

import lombok.Data;

/**
 * UserID DTO which returns a userId
 */
@Data
public class UserIdDto {

  private long userId;

  public UserIdDto(long userId) {
    this.userId = userId;
  }

  public UserIdDto() {
  }
}
