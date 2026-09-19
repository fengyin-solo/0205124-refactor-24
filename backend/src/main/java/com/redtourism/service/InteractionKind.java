package com.redtourism.service;

/**
 * 开关型互动类型（收藏、点赞）。
 * 各类型只保留自己的差异（重复操作提示语），
 * 新增同类互动时在此登记即可复用同一套处理。
 */
public enum InteractionKind {
    FAVORITE("已收藏"),
    LIKE("已点赞");

    private final String duplicateMessage;

    InteractionKind(String duplicateMessage) {
        this.duplicateMessage = duplicateMessage;
    }

    public String getDuplicateMessage() {
        return duplicateMessage;
    }
}
