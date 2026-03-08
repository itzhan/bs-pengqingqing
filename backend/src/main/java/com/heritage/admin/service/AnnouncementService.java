package com.heritage.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.AnnouncementDTO;
import com.heritage.admin.entity.Announcement;

import java.util.List;

public interface AnnouncementService extends IService<Announcement> {
    void createAnnouncement(Long publisherId, AnnouncementDTO dto);
    void updateAnnouncement(Long id, AnnouncementDTO dto);
    void deleteAnnouncement(Long id);
    PageResult<Announcement> listAnnouncements(int page, int size, Integer status);
    List<Announcement> listPublished();
}
