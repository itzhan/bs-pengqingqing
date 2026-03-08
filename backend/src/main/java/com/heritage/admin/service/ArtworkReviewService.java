package com.heritage.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heritage.admin.dto.ArtworkReviewDTO;
import com.heritage.admin.entity.ArtworkReview;

import java.util.List;

public interface ArtworkReviewService extends IService<ArtworkReview> {
    void createReview(Long reviewerId, ArtworkReviewDTO dto);
    List<ArtworkReview> getByArtworkId(Long artworkId);
}
