package com.redtourism.interaction;

import java.util.List;

/**
 * “用户 ↔ 内容”单条互动记录（收藏、点赞……）的统一处理契约。
 * 新增 / 取消 / 是否已操作 / 计数 / 用户列表的判断流程全部相同，
 * 具体种类的差异由 {@link AbstractUserInteractionHandler} 的子类提供。
 */
public interface UserInteractionHandler<T extends UserInteractionRecord> {

    InteractionType getInteractionType();

    /** 新增互动：内容类型不支持抛 IllegalArgumentException，重复操作抛出带种类提示的异常。 */
    boolean add(Long userId, String targetType, Long targetId);

    /** 取消互动：按 (用户, 内容) 定位，天然带归属校验；未操作过返回 false（幂等）。 */
    boolean remove(Long userId, String targetType, Long targetId);

    /** 判断当前用户是否已对该内容执行过此互动。 */
    boolean isInteracted(Long userId, String targetType, Long targetId);

    /** 某条内容收到的互动总数（不区分用户）。 */
    long count(String targetType, Long targetId);

    /** 某用户的互动列表，targetType 为空时返回全部类型。 */
    List<T> listByUser(Long userId, String targetType);
}
