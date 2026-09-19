package com.redtourism.interaction;

import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 互动处理器注册表：新增一种单条互动（收藏、点赞之外的）时，
 * 只要新增一个 {@link UserInteractionHandler} 实现并登记 {@link InteractionType}，
 * 这里会自动收录，Service 与 Controller 无需改动。
 */
@Component
public class InteractionHandlerRegistry {

    private final Map<InteractionType, UserInteractionHandler<?>> handlers = new EnumMap<>(InteractionType.class);

    public InteractionHandlerRegistry(List<UserInteractionHandler<?>> handlerList) {
        for (UserInteractionHandler<?> handler : handlerList) {
            handlers.put(handler.getInteractionType(), handler);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends UserInteractionRecord> UserInteractionHandler<T> getHandler(InteractionType type) {
        UserInteractionHandler<?> handler = handlers.get(type);
        if (handler == null) {
            throw new IllegalStateException("未找到互动处理器：" + type);
        }
        return (UserInteractionHandler<T>) handler;
    }
}
