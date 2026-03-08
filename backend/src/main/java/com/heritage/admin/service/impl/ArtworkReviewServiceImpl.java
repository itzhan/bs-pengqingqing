package com.heritage.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.admin.common.BusinessException;
import com.heritage.admin.dto.ArtworkReviewDTO;
import com.heritage.admin.entity.Artwork;
import com.heritage.admin.entity.ArtworkReview;
import com.heritage.admin.entity.GrowthRecord;
import com.heritage.admin.mapper.ArtworkMapper;
import com.heritage.admin.mapper.ArtworkReviewMapper;
import com.heritage.admin.mapper.GrowthRecordMapper;
import com.heritage.admin.service.ArtworkReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtworkReviewServiceImpl extends ServiceImpl<ArtworkReviewMapper, ArtworkReview> implements ArtworkReviewService {

    private final ArtworkMapper artworkMapper;
    private final GrowthRecordMapper growthRecordMapper;

    @Override
    public void createReview(Long reviewerId, ArtworkReviewDTO dto) {
        Artwork artwork = artworkMapper.selectById(dto.getArtworkId());
        if (artwork == null) {
            throw new BusinessException("作品不存在");
        }

        ArtworkReview review = new ArtworkReview();
        review.setArtworkId(dto.getArtworkId());
        review.setReviewerId(reviewerId);
        review.setContent(dto.getContent());
        review.setScore(dto.getScore());
        save(review);

        // Update artwork status to 2 (reviewed)
        artwork.setStatus(2);
        artworkMapper.updateById(artwork);

        // Create ARTWORK_REVIEWED growth record
        GrowthRecord record = new GrowthRecord();
        record.setApprenticeId(artwork.getApprenticeId());
        record.setRecordType("ARTWORK_REVIEWED");
        record.setTitle("作品被点评：" + artwork.getTitle());
        record.setDescription(dto.getContent());
        record.setRelatedId(dto.getArtworkId());
        record.setRelatedType("ARTWORK");
        growthRecordMapper.insert(record);
    }

    @Override
    public List<ArtworkReview> getByArtworkId(Long artworkId) {
        return list(new LambdaQueryWrapper<ArtworkReview>()
                .eq(ArtworkReview::getArtworkId, artworkId)
                .orderByDesc(ArtworkReview::getCreatedAt));
    }
}
