package com.heritage.admin.controller;

import com.heritage.admin.common.Result;
import com.heritage.admin.config.AuditOperation;
import com.heritage.admin.dto.SkillCategoryDTO;
import com.heritage.admin.entity.SkillCategory;
import com.heritage.admin.service.SkillCategoryService;
import com.heritage.admin.vo.SkillCategoryVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skill-categories")
@RequiredArgsConstructor
public class SkillCategoryController {

    private final SkillCategoryService categoryService;

    @GetMapping({"", "/"})
    public Result<?> listCategories() {
        List<SkillCategory> categories = categoryService.listAll();
        return Result.success(categories);
    }

    @GetMapping("/tree")
    public Result<?> listTree() {
        List<SkillCategoryVO> tree = categoryService.listTree();
        return Result.success(tree);
    }

    @PostMapping({"", "/"})
    @AuditOperation("创建技艺类别")
    public Result<?> createCategory(@Valid @RequestBody SkillCategoryDTO dto) {
        categoryService.createCategory(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> updateCategory(@PathVariable Long id, @Valid @RequestBody SkillCategoryDTO dto) {
        categoryService.updateCategory(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @AuditOperation("删除技艺类别")
    public Result<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
}
