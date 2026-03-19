package com.heritage.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.ArtworkDTO;
import com.heritage.admin.entity.Artwork;
import com.heritage.admin.vo.ArtworkVO;

public interface ArtworkService extends IService<Artwork> {
    void createArtwork(Long apprenticeId, ArtworkDTO dto);
    void updateArtwork(Long artworkId, Long apprenticeId, ArtworkDTO dto);
    void submitArtwork(Long artworkId, Long apprenticeId);
    PageResult<Artwork> listByApprentice(Long apprenticeId, int page, int size);
    PageResult<Artwork> listByMaster(Long masterId, int page, int size);
    PageResult<ArtworkVO> listAll(int page, int size, Integer status);
}

