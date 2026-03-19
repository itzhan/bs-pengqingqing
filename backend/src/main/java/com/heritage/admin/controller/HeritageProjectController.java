package com.heritage.admin.controller;

import com.heritage.admin.common.PageResult;
import com.heritage.admin.common.Result;
import com.heritage.admin.config.AuditOperation;
import com.heritage.admin.dto.HeritageProjectDTO;
import com.heritage.admin.entity.HeritageProject;
import com.heritage.admin.service.HeritageProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/heritage-projects")
@RequiredArgsConstructor
public class HeritageProjectController {

    private final HeritageProjectService projectService;

    @GetMapping({"", "/"})
    public Result<?> listProjects(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String level) {
        PageResult<HeritageProject> result = projectService.listProjects(page, size, keyword, categoryId, level);
        return Result.success(result);
    }

    @GetMapping("/all")
    public Result<?> listAllProjects() {
        List<HeritageProject> projects = projectService.listAll();
        return Result.success(projects);
    }

    @GetMapping("/{id}")
    public Result<?> getProject(@PathVariable Long id) {
        HeritageProject project = projectService.getById(id);
        return Result.success(project);
    }

    @PostMapping({"", "/"})
    @AuditOperation("创建非遗项目")
    public Result<?> createProject(@Valid @RequestBody HeritageProjectDTO dto) {
        projectService.createProject(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> updateProject(@PathVariable Long id, @Valid @RequestBody HeritageProjectDTO dto) {
        projectService.updateProject(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @AuditOperation("删除非遗项目")
    public Result<?> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return Result.success();
    }
}
