package com.redtourism.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.redtourism.entity.Comment;
import com.redtourism.entity.UserInteraction;
import java.util.List;

public interface InteractionService {

    /* ========== 留言 ========== */
    boolean addComment(Comment comment);
    IPage<Comment> listComments(int page, int size, String targetType, Long targetId);
    IPage<Comment> listAllComments(int page, int size, String keyword);
    Comment getCommentById(Long id);
    boolean replyComment(Long commentId, String replyContent, Long adminId);
    /**
     * 删除留言。admin 为 false 时校验归属，仅允许删除本人的留言。
     */
    boolean deleteComment(Long id, Long operatorId, boolean admin);

    /* ========== 开关型互动（收藏/点赞，共用一套处理） ========== */
    boolean addInteraction(InteractionKind kind, Long userId, String targetType, Long targetId);
    boolean removeInteraction(InteractionKind kind, Long userId, String targetType, Long targetId);
    boolean hasInteraction(InteractionKind kind, Long userId, String targetType, Long targetId);
    long countInteractions(InteractionKind kind, String targetType, Long targetId);
    <T extends UserInteraction> List<T> listUserInteractions(InteractionKind kind, Long userId, String targetType);
}
