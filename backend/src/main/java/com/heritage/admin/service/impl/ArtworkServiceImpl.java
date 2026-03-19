package com.heritage.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heritage.admin.common.BusinessException;
import com.heritage.admin.common.PageResult;
import com.heritage.admin.dto.ArtworkDTO;
import com.heritage.admin.entity.*;
import com.heritage.admin.mapper.ArtworkMapper;
import com.heritage.admin.mapper.GrowthRecordMapper;
import com.heritage.admin.mapper.HeritageProjectMapper;
import com.heritage.admin.mapper.MasterApprenticeRelationMapper;
import com.heritage.admin.mapper.SysUserMapper;
import com.heritage.admin.service.ArtworkService;
import com.heritage.admin.vo.ArtworkVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtworkServiceImpl extends ServiceImpl<ArtworkMapper, Artwork> implements ArtworkService {

    private final MasterApprenticeRelationMapper masterApprenticeRelationMapper;
    private final GrowthRecordMapper growthRecordMapper;
    private final SysUserMapper sysUserMapper;
    private final HeritageProjectMapper heritageProjectMapper;

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
    public PageResult<ArtworkVO> listAll(int page, int size, Integer status) {
        Page<Artwork> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Artwork> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Artwork::getStatus, status);
        }

        wrapper.orderByDesc(Artwork::getCreatedAt);
        Page<Artwork> result = page(pageParam, wrapper);

        // 批量查询创作者姓名
        List<Long> apprenticeIds = result.getRecords().stream()
                .map(Artwork::getApprenticeId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, SysUser> userMap = java.util.Collections.emptyMap();
        if (!apprenticeIds.isEmpty()) {
            List<SysUser> users = sysUserMapper.selectBatchIds(apprenticeIds);
            userMap = users.stream()
                    .collect(Collectors.toMap(SysUser::getId, user -> user));
        }

        // 批量查询所属项目名
        List<Long> projectIds = result.getRecords().stream()
                .map(Artwork::getHeritageProjectId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, HeritageProject> projectMap = java.util.Collections.emptyMap();
        if (!projectIds.isEmpty()) {
            List<HeritageProject> projects = heritageProjectMapper.selectBatchIds(projectIds);
            projectMap = projects.stream()
                    .collect(Collectors.toMap(HeritageProject::getId, project -> project));
        }

        Map<Long, SysUser> finalUserMap = userMap;
        Map<Long, HeritageProject> finalProjectMap = projectMap;
        List<ArtworkVO> voList = result.getRecords().stream().map(a -> {
            ArtworkVO vo = new ArtworkVO();
            BeanUtils.copyProperties(a, vo);
            if (a.getApprenticeId() != null) {
                SysUser apprentice = finalUserMap.get(a.getApprenticeId());
                if (apprentice != null) {
                    vo.setApprenticeName(apprentice.getRealName() != null ? apprentice.getRealName() : apprentice.getUsername());
                    vo.setApprenticeAvatar(apprentice.getAvatar());
                } else {
                    vo.setApprenticeName("未知用户");
                }
            }
            if (a.getHeritageProjectId() != null) {
                HeritageProject project = finalProjectMap.get(a.getHeritageProjectId());
                if (project != null) {
                    vo.setProjectName(project.getName());
                    vo.setProjectImageUrl(project.getImageUrl());
                } else {
                    vo.setProjectName("未知项目");
                }
            }
            // 解析 imageUrls JSON 取第一张图
            if (a.getImageUrls() != null && !a.getImageUrls().isEmpty()) {
                String urls = a.getImageUrls().trim();
                if (urls.startsWith("[")) {
                    // JSON数组格式
                    urls = urls.substring(1);
                    if (urls.endsWith("]")) urls = urls.substring(0, urls.length() - 1);
                    String[] parts = urls.split(",");
                    if (parts.length > 0) {
                        String firstUrl = parts[0].trim().replaceAll("^\"|\"$", "");
                        vo.setFirstImageUrl(firstUrl);
                    }
                } else {
                    // 单个URL或逗号分隔
                    String[] parts = urls.split(",");
                    vo.setFirstImageUrl(parts[0].trim());
                }
            }
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }
}
