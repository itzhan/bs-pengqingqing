package com.heritage.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.PasswordUpdateDTO;
import com.heritage.admin.dto.RegisterDTO;
import com.heritage.admin.dto.UserUpdateDTO;
import com.heritage.admin.entity.SysUser;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface SysUserService extends IService<SysUser> {
    SysUser getByUsername(String username);
    SysUser register(RegisterDTO dto, PasswordEncoder encoder);
    void updateProfile(Long userId, UserUpdateDTO dto);
    void updatePassword(Long userId, PasswordUpdateDTO dto, PasswordEncoder encoder);
    PageResult<SysUser> listUsers(int page, int size, String role, String keyword);
    void updateStatus(Long userId, Integer status);
}
