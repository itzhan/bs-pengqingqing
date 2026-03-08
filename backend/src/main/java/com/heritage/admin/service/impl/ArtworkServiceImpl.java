package com.heritage.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.admin.common.BusinessException;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.ArtworkDTO;
import com.heritage.admin.entity.Artwork;
import com.heritage.admin.entity.GrowthRecord;
import com.heritage.admin.entity.MasterApprenticeRelation;
import com.heritage.admin.mapper.ArtworkMapper;
import com.heritage.admin.mapper.GrowthRecordMapper;
import com.heritage.admin.mapper.MasterApprenticeRelationMapper;
import com.heritage.admin.service.ArtworkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtworkServiceImpl extends ServiceImpl<ArtworkMapper, Artwork> implements ArtworkService {

    private final MasterApprenticeRelationMapper masterApprenticeRelationMapper;
    private final GrowthRecordMapper growthRecordMapper;

    @Override
    public void createArtwork(Long apprenticeId, ArtworkDTO dto) {
        // Find master from active relation
        MasterApprenticeRelation relation = masterApprenticeRelationMapper.selectOne(
                new LambdaQueryWrapper<MasterApprenticeRelation>()
                        .eq(MasterApprenticeRelation::getApprenticeId, apprenticeId)
                        .eq(MasterApprenticeRelation::getStatus, 1)
                        .last("LIMIT 1"));
        if (relation == null) {
            throw new BusinessException("未找到有效的师徒关系");
        }

        Artwork artwork = new Artwork();
        artwork.setTitle(dto.getTitle());
        artwork.setDescription(dto.getDescription());
        artwork.setApprenticeId(apprenticeId);
        artwork.setMasterId(relation.getMasterId());
        artwork.setHeritageProjectId(dto.getHeritageProjectId());
        artwork.setSkillCategoryId(dto.getSkillCategoryId());
        artwork.setImageUrls(dto.getImageUrls());
        artwork.setVideoUrl(dto.getVideoUrl());
        artwork.setStatus(0); // draft
        save(artwork);
    }

    @Override
    public void updateArtwork(Long artworkId, Long apprenticeId, ArtworkDTO dto) {
        Artwork artwork = getById(artworkId);
        if (artwork == null) {
            throw new BusinessException("作品不存在");
        }

        if (!artwork.getApprenticeId().equals(apprenticeId)) {
            throw new BusinessException("无权操作");
        }

        artwork.setTitle(dto.getTitle());
        artwork.setDescription(dto.getDescription());
        artwork.setHeritageProjectId(dto.getHeritageProjectId());
        artwork.setSkillCategoryId(dto.getSkillCategoryId());
        artwork.setImageUrls(dto.getImageUrls());
        artwork.setVideoUrl(dto.getVideoUrl());
        updateById(artwork);
    }

    @Override
    public void submitArtwork(Long artworkId, Long apprenticeId) {
        Artwork artwork = getById(artworkId);
        if (artwork == null) {
            throw new BusinessException("作品不存在");
        }

        if (!artwork.getApprenticeId().equals(apprenticeId)) {
            throw new BusinessException("无权操作");
        }

        artwork.setStatus(1); // submitted
        updateById(artwork);

        // Create ARTWORK_SUBMIT growth record
        GrowthRecord record = new GrowthRecord();
        record.setApprenticeId(apprenticeId);
        record.setRecordType("ARTWORK_SUBMIT");
        record.setTitle("提交作品：" + artwork.getTitle());
        record.setDescription(artwork.getDescription());
        record.setRelatedId(artworkId);
        record.setRelatedType("ARTWORK");
        growthRecordMapper.insert(record);
    }

    @Override
    public PageResult<Artwork> listByApprentice(Long apprenticeId, int page, int size) {
        Page<Artwork> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Artwork> wrapper = new LambdaQueryWrapper<Artwork>()
                .eq(Artwork::getApprenticeId, apprenticeId)
                .orderByDesc(Artwork::getCreatedAt);
        Page<Artwork> result = page(pageParam, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public PageResult<Artwork> listByMaster(Long masterId, int page, int size) {
        // Get master's apprentices
        List<MasterApprenticeRelation> relations = masterApprenticeRelationMapper.selectList(
                new LambdaQueryWrapper<MasterApprenticeRelation>()
                        .eq(MasterApprenticeRelation::getMasterId, masterId)
                        .eq(MasterApprenticeRelation::getStatus, 1));
        List<Long> apprenticeIds = relations.stream()
                .map(MasterApprenticeRelation::getApprenticeId)
                .collect(Collectors.toList());

        if (apprenticeIds.isEmpty()) {
            return new PageResult<>(List.of(), 0, page, size);
        }

        Page<Artwork> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Artwork> wrapper = new LambdaQueryWrapper<Artwork>()
                .in(Artwork::getApprenticeId, apprenticeIds)
                .orderByDesc(Artwork::getCreatedAt);
        Page<Artwork> result = page(pageParam, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public PageResult<Artwork> listAll(int page, int size, Integer status) {
        Page<Artwork> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Artwork> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Artwork::getStatus, status);
        }

        wrapper.orderByDesc(Artwork::getCreatedAt);
        Page<Artwork> result = page(pageParam, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
}
