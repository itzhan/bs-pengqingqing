package com.heritage.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.HeritageProjectDTO;
import com.heritage.admin.entity.HeritageProject;

import java.util.List;

public interface HeritageProjectService extends IService<HeritageProject> {
    PageResult<HeritageProject> listProjects(int page, int size, String keyword, Long categoryId, String level);
    List<HeritageProject> listAll();
    void createProject(HeritageProjectDTO dto);
    void updateProject(Long id, HeritageProjectDTO dto);
    void deleteProject(Long id);
}
