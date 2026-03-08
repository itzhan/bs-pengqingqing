package com.heritage.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.admin.common.BusinessException;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.AnnouncementDTO;
import com.heritage.admin.entity.Announcement;
import com.heritage.admin.mapper.AnnouncementMapper;
import com.heritage.admin.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Override
    public void createAnnouncement(Long publisherId, AnnouncementDTO dto) {
        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setPublisherId(publisherId);
        announcement.setIsTop(dto.getIsTop() != null ? dto.getIsTop() : 0);
        announcement.setStatus(dto.getStatus() != null ? dto.getStatus() : 0);
        save(announcement);
    }

    @Override
    public void updateAnnouncement(Long id, AnnouncementDTO dto) {
        Announcement announcement = getById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }

        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        if (dto.getIsTop() != null) {
            announcement.setIsTop(dto.getIsTop());
        }
        if (dto.getStatus() != null) {
            announcement.setStatus(dto.getStatus());
        }
        updateById(announcement);
    }

    @Override
    public void deleteAnnouncement(Long id) {
        Announcement announcement = getById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        removeById(id);
    }

    @Override
    public PageResult<Announcement> listAnnouncements(int page, int size, Integer status) {
        Page<Announcement> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Announcement::getStatus, status);
        }

        wrapper.orderByDesc(Announcement::getIsTop)
                .orderByDesc(Announcement::getCreatedAt);
        Page<Announcement> result = page(pageParam, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public List<Announcement> listPublished() {
        return list(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getStatus, 1)
                .orderByDesc(Announcement::getIsTop)
                .orderByDesc(Announcement::getCreatedAt));
    }
}
