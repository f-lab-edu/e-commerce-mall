package com.ecommerce.addressbook.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddressbookRequest {

  private String name;
  private String address;
  private String phone;
  private Integer isDefault;
}
