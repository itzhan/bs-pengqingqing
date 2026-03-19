package com.heritage.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.admin.common.BusinessException;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.MasterProfileDTO;
import com.heritage.admin.entity.HeritageProject;
import com.heritage.admin.entity.MasterProfile;
import com.heritage.admin.entity.SkillCategory;
import com.heritage.admin.entity.SysUser;
import com.heritage.admin.mapper.HeritageProjectMapper;
import com.heritage.admin.mapper.MasterProfileMapper;
import com.heritage.admin.mapper.SkillCategoryMapper;
import com.heritage.admin.mapper.SysUserMapper;
import com.heritage.admin.service.MasterProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MasterProfileServiceImpl extends ServiceImpl<MasterProfileMapper, MasterProfile> implements MasterProfileService {

    private final SysUserMapper sysUserMapper;
    private final HeritageProjectMapper heritageProjectMapper;
    private final SkillCategoryMapper skillCategoryMapper;

    @Override
    public MasterProfile getByUserId(Long userId) {
        MasterProfile profile = getOne(new LambdaQueryWrapper<MasterProfile>()
                .eq(MasterProfile::getUserId, userId));
        enrichProfiles(profile == null ? List.of() : List.of(profile));
        return profile;
    }

    @Override
    public void saveOrUpdateProfile(Long userId, MasterProfileDTO dto) {
        MasterProfile profile = getByUserId(userId);
        if (profile == null) {
            profile = new MasterProfile();
            profile.setUserId(userId);
        }

        profile.setTitle(dto.getTitle());
        profile.setHeritageProjectId(dto.getHeritageProjectId());
        profile.setSkillCategoryId(dto.getSkillCategoryId());
        profile.setCareerYears(dto.getCareerYears());
        profile.setCareerHistory(dto.getCareerHistory());
        profile.setSpecialties(dto.getSpecialties());
        profile.setRepresentativeWorks(dto.getRepresentativeWorks());
        profile.setBio(dto.getBio());
        profile.setHonor(dto.getHonor());
        profile.setAuditStatus(0); // pending

        saveOrUpdate(profile);
    }

    @Override
    public void auditProfile(Long profileId, Integer auditStatus) {
        MasterProfile profile = getById(profileId);
        if (profile == null) {
            throw new BusinessException("档案不存在");
        }
        profile.setAuditStatus(auditStatus);
        updateById(profile);
    }

    @Override
    public PageResult<MasterProfile> listProfiles(int page, int size, Integer auditStatus) {
        Page<MasterProfile> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<MasterProfile> wrapper = new LambdaQueryWrapper<>();

        if (auditStatus != null) {
            wrapper.eq(MasterProfile::getAuditStatus, auditStatus);
        }

        wrapper.orderByDesc(MasterProfile::getCreatedAt);
        Page<MasterProfile> result = page(pageParam, wrapper);
        enrichProfiles(result.getRecords());
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    private void enrichProfiles(List<MasterProfile> profiles) {
        if (profiles == null || profiles.isEmpty()) {
            return;
        }

        List<Long> userIds = profiles.stream()
                .map(MasterProfile::getUserId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, SysUser> userMap = Collections.emptyMap();
        if (!userIds.isEmpty()) {
            userMap = sysUserMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(SysUser::getId, user -> user));
        }

        List<Long> projectIds = profiles.stream()
                .map(MasterProfile::getHeritageProjectId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, HeritageProject> projectMap = Collections.emptyMap();
        if (!projectIds.isEmpty()) {
            projectMap = heritageProjectMapper.selectBatchIds(projectIds).stream()
                    .collect(Collectors.toMap(HeritageProject::getId, project -> project));
        }

        List<Long> skillCategoryIds = profiles.stream()
                .map(MasterProfile::getSkillCategoryId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, SkillCategory> skillCategoryMap = Collections.emptyMap();
        if (!skillCategoryIds.isEmpty()) {
            skillCategoryMap = skillCategoryMapper.selectBatchIds(skillCategoryIds).stream()
                    .collect(Collectors.toMap(SkillCategory::getId, category -> category));
        }

        for (MasterProfile profile : profiles) {
            SysUser user = userMap.get(profile.getUserId());
            if (user != null) {
                profile.setRealName(user.getRealName());
                profile.setAvatar(user.getAvatar());
            }
            HeritageProject project = projectMap.get(profile.getHeritageProjectId());
            if (project != null) {
                profile.setProjectName(project.getName());
            }
            SkillCategory category = skillCategoryMap.get(profile.getSkillCategoryId());
            if (category != null) {
                profile.setSkillCategoryName(category.getName());
            }
        }
    }
}
