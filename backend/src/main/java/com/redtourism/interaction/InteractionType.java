package com.redtourism.interaction;

import java.util.EnumSet;
import java.util.Set;

/**
 * 互动种类。各内容类型（{@link TargetType}）支持哪些互动在这里声明，
 * 新增一种可互动内容时只需调整各枚举值的 supportedTargets。
 */
public enum InteractionType {

    FAVORITE("收藏", EnumSet.of(TargetType.SPOT, TargetType.ROUTE, TargetType.CULTURE)),
    LIKE("点赞", EnumSet.of(TargetType.CULTURE)),
    COMMENT("留言", EnumSet.of(TargetType.SPOT, TargetType.HOTEL));

    private final String actionName;
    private final Set<TargetType> supportedTargets;

    InteractionType(String actionName, EnumSet<TargetType> supportedTargets) {
        this.actionName = actionName;
        this.supportedTargets = supportedTargets;
    }

    public String getActionName() {
        return actionName;
    }

    public boolean supports(TargetType targetType) {
        return targetType != null && supportedTargets.contains(targetType);
    }

    /**
     * 统一的内容类型校验：解析类型并确认该互动支持此内容类型，
     * 非法或不支持时返回 400 风格的参数错误，避免脏记录写入。
     */
    public TargetType requireTarget(String targetType) {
        TargetType type = TargetType.of(targetType);
        if (type == null) {
            throw new IllegalArgumentException("不支持的内容类型：" + targetType);
        }
        if (!supports(type)) {
            throw new IllegalArgumentException(actionName + "暂不支持该内容类型");
        }
        return type;
    }
}
