package com.redtourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.redtourism.entity.*;
import com.redtourism.mapper.*;
import com.redtourism.service.InteractionKind;
import com.redtourism.service.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class InteractionServiceImpl implements InteractionService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private LikeRecordMapper likeRecordMapper;
    @Autowired
    private UserMapper userMapper;

    /** 开关型互动的共用处理入口：各互动类型只登记自己的表与实体 */
    private final Map<InteractionKind, ToggleInteractionSupport<? extends UserInteraction>> toggleSupports =
            new EnumMap<>(InteractionKind.class);

    @PostConstruct
    void initToggleSupports() {
        toggleSupports.put(InteractionKind.FAVORITE, new ToggleInteractionSupport<>(favoriteMapper, Favorite::new));
        toggleSupports.put(InteractionKind.LIKE, new ToggleInteractionSupport<>(likeRecordMapper, LikeRecord::new));
    }

    private ToggleInteractionSupport<? extends UserInteraction> toggle(InteractionKind kind) {
        ToggleInteractionSupport<? extends UserInteraction> support = toggleSupports.get(kind);
        if (support == null) {
            throw new IllegalArgumentException("不支持的互动类型: " + kind);
        }
        return support;
    }

    /* ========== 留言 ========== */

    @Override
    public boolean addComment(Comment comment) {
        return commentMapper.insert(comment) > 0;
    }

    @Override
    public IPage<Comment> listComments(int page, int size, String targetType, Long targetId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(targetType)) {
            wrapper.eq(Comment::getTargetType, targetType);
        }
        if (targetId != null) {
            wrapper.eq(Comment::getTargetId, targetId);
        }
        wrapper.orderByDesc(Comment::getCreateTime);
        IPage<Comment> result = commentMapper.selectPage(new Page<>(page, size), wrapper);
        fillUserInfo(result.getRecords());
        return result;
    }

    @Override
    public IPage<Comment> listAllComments(int page, int size, String keyword) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Comment::getContent, keyword);
        }
        wrapper.orderByDesc(Comment::getCreateTime);
        IPage<Comment> result = commentMapper.selectPage(new Page<>(page, size), wrapper);
        fillUserInfo(result.getRecords());
        return result;
    }

    /** 为留言列表填充用户昵称与头像 */
    private void fillUserInfo(List<Comment> comments) {
        comments.forEach(c -> {
            User user = userMapper.selectById(c.getUserId());
            if (user != null) {
                c.setUsername(user.getNickname() != null ? user.getNickname() : user.getUsername());
                c.setUserAvatar(user.getAvatar());
            }
        });
    }

    @Override
    public Comment getCommentById(Long id) {
        return commentMapper.selectById(id);
    }

    @Override
    public boolean replyComment(Long commentId, String replyContent, Long adminId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("留言不存在");
        }
        comment.setReplyContent(replyContent);
        comment.setReplyTime(new Date());
        return commentMapper.updateById(comment) > 0;
    }

    @Override
    public boolean deleteComment(Long id, Long operatorId, boolean admin) {
        if (!admin) {
            Comment comment = commentMapper.selectById(id);
            if (comment == null) {
                throw new RuntimeException("留言不存在");
            }
            if (comment.getUserId() == null || !comment.getUserId().equals(operatorId)) {
                throw new RuntimeException("只能删除自己的留言");
            }
        }
        return commentMapper.deleteById(id) > 0;
    }

    /* ========== 开关型互动（收藏/点赞） ========== */

    @Override
    public boolean addInteraction(InteractionKind kind, Long userId, String targetType, Long targetId) {
        if (hasInteraction(kind, userId, targetType, targetId)) {
            throw new RuntimeException(kind.getDuplicateMessage());
        }
        return toggle(kind).insert(userId, targetType, targetId) > 0;
    }

    @Override
    public boolean removeInteraction(InteractionKind kind, Long userId, String targetType, Long targetId) {
        return toggle(kind).delete(userId, targetType, targetId) > 0;
    }

    @Override
    public boolean hasInteraction(InteractionKind kind, Long userId, String targetType, Long targetId) {
        return toggle(kind).exists(userId, targetType, targetId);
    }

    @Override
    public long countInteractions(InteractionKind kind, String targetType, Long targetId) {
        return toggle(kind).count(targetType, targetId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends UserInteraction> List<T> listUserInteractions(InteractionKind kind, Long userId, String targetType) {
        return (List<T>) toggle(kind).listByUser(userId, targetType);
    }
}
