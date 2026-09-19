package com.redtourism.interaction;

/**
 * “用户对某内容的一条互动记录”（收藏、点赞……）的公共标记。
 * 各记录实体实现本接口即可复用统一的互动处理流程，实体字段与表结构保持不变。
 */
public interface UserInteractionRecord {
    Long getUserId();
    void setUserId(Long userId);

    String getTargetType();
    void setTargetType(String targetType);

    Long getTargetId();
    void setTargetId(Long targetId);
}
