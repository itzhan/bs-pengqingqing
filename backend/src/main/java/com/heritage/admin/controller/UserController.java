package com.heritage.admin.controller;

import com.heritage.admin.common.PageResult;
import com.heritage.admin.common.Result;
import com.heritage.admin.config.AuditOperation;
import com.heritage.admin.dto.PasswordUpdateDTO;
import com.heritage.admin.dto.UserUpdateDTO;
import com.heritage.admin.entity.SysUser;
import com.heritage.admin.service.SysUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final SysUserService userService;
    private final PasswordEncoder passwordEncoder;

    private SysUser getCurrentUser() {
        return (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping("/")
    public Result<?> listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String keyword) {
        PageResult<SysUser> result = userService.listUsers(page, size, role, keyword);
        result.getRecords().forEach(user -> user.setPassword(null));
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<?> getUser(@PathVariable Long id) {
        SysUser user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PutMapping("/profile")
    public Result<?> updateProfile(@Valid @RequestBody UserUpdateDTO dto) {
        SysUser currentUser = getCurrentUser();
        userService.updateProfile(currentUser.getId(), dto);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<?> updatePassword(@Valid @RequestBody PasswordUpdateDTO dto) {
        SysUser currentUser = getCurrentUser();
        userService.updatePassword(currentUser.getId(), dto, passwordEncoder);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @AuditOperation("更新用户状态")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }
}
