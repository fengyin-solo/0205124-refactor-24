package com.redtourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.redtourism.entity.UserInteraction;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.function.Supplier;

/**
 * 开关型互动（收藏、点赞等）的共用数据访问。
 * 各互动记录的表结构一致（user_id + target_type + target_id + create_time），
 * 差异仅在于对应的表与实体类型，由构造参数注入。
 */
public class ToggleInteractionSupport<T extends UserInteraction> {

    private final BaseMapper<T> mapper;
    private final Supplier<T> factory;

    public ToggleInteractionSupport(BaseMapper<T> mapper, Supplier<T> factory) {
        this.mapper = mapper;
        this.factory = factory;
    }

    /** 是否已操作过 */
    public boolean exists(Long userId, String targetType, Long targetId) {
        return mapper.selectCount(userTargetQuery(userId, targetType, targetId)) > 0;
    }

    /** 新增一条互动记录 */
    public int insert(Long userId, String targetType, Long targetId) {
        T record = factory.get();
        record.setUserId(userId);
        record.setTargetType(targetType);
        record.setTargetId(targetId);
        return mapper.insert(record);
    }

    /** 取消（删除）一条互动记录 */
    public int delete(Long userId, String targetType, Long targetId) {
        return mapper.delete(userTargetQuery(userId, targetType, targetId));
    }

    /** 某内容被操作的次数 */
    public long count(String targetType, Long targetId) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        wrapper.eq("target_type", targetType)
                .eq("target_id", targetId);
        return mapper.selectCount(wrapper);
    }

    /** 某用户的互动记录列表，可按内容类型过滤 */
    public List<T> listByUser(Long userId, String targetType) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        if (StringUtils.hasText(targetType)) {
            wrapper.eq("target_type", targetType);
        }
        wrapper.orderByDesc("create_time");
        return mapper.selectList(wrapper);
    }

    private QueryWrapper<T> userTargetQuery(Long userId, String targetType, Long targetId) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("target_type", targetType)
                .eq("target_id", targetId);
        return wrapper;
    }
}
