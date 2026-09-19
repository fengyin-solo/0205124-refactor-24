package com.redtourism.interaction;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * “用户 ↔ 内容”单条互动记录的共用处理流程。
 *
 * <p>新增 / 取消 / 是否已操作 / 计数 / 用户列表的判断完全一致，
 * 具体种类（收藏、点赞……）只需提供三个差异点：
 * <ol>
 *     <li>{@link #getInteractionType()}：种类元数据（提示语、支持的内容类型）</li>
 *     <li>{@link #getMapper()} / {@link #newRecord()}：记录实体与 Mapper</li>
 *     <li>{@link #userIdColumn()} / {@link #targetTypeColumn()} / {@link #targetIdColumn()}：
 *     实体字段，供统一拼装查询条件</li>
 * </ol>
 */
public abstract class AbstractUserInteractionHandler<T extends UserInteractionRecord>
        implements UserInteractionHandler<T> {

    protected abstract BaseMapper<T> getMapper();

    /** 创建一条空白记录，create_time 等字段由 MyBatis-Plus 自动填充。 */
    protected abstract T newRecord();

    protected abstract SFunction<T, Long> userIdColumn();

    protected abstract SFunction<T, String> targetTypeColumn();

    protected abstract SFunction<T, Long> targetIdColumn();

    @Override
    public boolean add(Long userId, String targetType, Long targetId) {
        TargetType type = getInteractionType().requireTarget(targetType);
        if (targetId == null) {
            throw new IllegalArgumentException("内容ID不能为空");
        }
        // 重复操作判断流程统一：已操作过直接给出种类对应的提示
        if (isInteracted(userId, type.name(), targetId)) {
            throw new RuntimeException("已" + getInteractionType().getActionName());
        }
        T record = newRecord();
        record.setUserId(userId);
        record.setTargetType(type.name());
        record.setTargetId(targetId);
        return getMapper().insert(record) > 0;
    }

    @Override
    public boolean remove(Long userId, String targetType, Long targetId) {
        TargetType type = getInteractionType().requireTarget(targetType);
        if (targetId == null) {
            throw new IllegalArgumentException("内容ID不能为空");
        }
        // 条件中始终带 userId，取消操作天然完成归属校验
        LambdaQueryWrapper<T> wrapper = targetWrapper(userId, type.name(), targetId);
        return getMapper().delete(wrapper) > 0;
    }

    @Override
    public boolean isInteracted(Long userId, String targetType, Long targetId) {
        if (userId == null || !StringUtils.hasText(targetType) || targetId == null) {
            return false;
        }
        return getMapper().selectCount(targetWrapper(userId, targetType, targetId)) > 0;
    }

    @Override
    public long count(String targetType, Long targetId) {
        if (!StringUtils.hasText(targetType) || targetId == null) {
            return 0;
        }
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(targetTypeColumn(), targetType)
                .eq(targetIdColumn(), targetId);
        return getMapper().selectCount(wrapper);
    }

    @Override
    public List<T> listByUser(Long userId, String targetType) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userIdColumn(), userId);
        if (StringUtils.hasText(targetType)) {
            // 列表筛选时同样只允许受支持的内容类型
            TargetType type = getInteractionType().requireTarget(targetType);
            wrapper.eq(targetTypeColumn(), type.name());
        }
        wrapper.orderByDesc(createTimeColumn());
        return getMapper().selectList(wrapper);
    }

    /** 记录的创建时间列，用户列表统一按创建时间倒序。 */
    protected abstract SFunction<T, ?> createTimeColumn();

    /** 统一的 (用户, 内容类型, 内容ID) 定位条件。 */
    private LambdaQueryWrapper<T> targetWrapper(Long userId, String targetType, Long targetId) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userIdColumn(), userId)
                .eq(targetTypeColumn(), targetType)
                .eq(targetIdColumn(), targetId);
        return wrapper;
    }
}
