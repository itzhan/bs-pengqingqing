package com.heritage.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.admin.common.BusinessException;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.MasterProfileDTO;
import com.heritage.admin.entity.MasterProfile;
import com.heritage.admin.mapper.MasterProfileMapper;
import com.heritage.admin.service.MasterProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MasterProfileServiceImpl extends ServiceImpl<MasterProfileMapper, MasterProfile> implements MasterProfileService {

    @Override
    public MasterProfile getByUserId(Long userId) {
        return getOne(new LambdaQueryWrapper<MasterProfile>()
                .eq(MasterProfile::getUserId, userId));
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
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
}
