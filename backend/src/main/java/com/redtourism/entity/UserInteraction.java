package com.redtourism.entity;

/**
 * 用户对内容的一次开关型互动（收藏、点赞等）的公共结构。
 * 各互动记录的表结构一致（user_id + target_type + target_id），
 * 实现该接口后即可复用同一套新增/取消/查询处理。
 */
public interface UserInteraction {
    Long getUserId();
    void setUserId(Long userId);
    String getTargetType();
    void setTargetType(String targetType);
    Long getTargetId();
    void setTargetId(Long targetId);
}
