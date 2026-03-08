package com.heritage.admin.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String realName;
    private String avatar;
    private String phone;
    private String email;
    private Integer gender;
}
