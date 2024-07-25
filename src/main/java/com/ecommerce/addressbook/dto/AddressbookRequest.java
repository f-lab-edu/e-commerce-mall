package com.ecommerce.addressbook.dto;

import com.ecommerce.common.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddressbookRequest extends BaseTimeEntity {

  private String name;
  private String address;
  private String phone;
  private Integer isDefault;
}
