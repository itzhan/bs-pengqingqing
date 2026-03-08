package com.heritage.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.MasterProfileDTO;
import com.heritage.admin.entity.MasterProfile;

public interface MasterProfileService extends IService<MasterProfile> {
    MasterProfile getByUserId(Long userId);
    void saveOrUpdateProfile(Long userId, MasterProfileDTO dto);
    void auditProfile(Long profileId, Integer auditStatus);
    PageResult<MasterProfile> listProfiles(int page, int size, Integer auditStatus);
}
