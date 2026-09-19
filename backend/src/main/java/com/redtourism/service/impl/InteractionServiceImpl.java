package com.redtourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.redtourism.entity.*;
import com.redtourism.interaction.InteractionHandlerRegistry;
import com.redtourism.interaction.InteractionType;
import com.redtourism.interaction.TargetType;
import com.redtourism.interaction.UserInteractionHandler;
import com.redtourism.mapper.*;
import com.redtourism.service.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class InteractionServiceImpl implements InteractionService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private InteractionHandlerRegistry handlerRegistry;

    private UserInteractionHandler<Favorite> favoriteHandler() {
        return handlerRegistry.getHandler(InteractionType.FAVORITE);
    }

    private UserInteractionHandler<LikeRecord> likeHandler() {
        return handlerRegistry.getHandler(InteractionType.LIKE);
    }

    // ==================== 留言 ====================

    @Override
    public boolean addComment(Comment comment) {
        // 与收藏/点赞走同一套内容类型登记与校验
        TargetType targetType = InteractionType.COMMENT.requireTarget(comment.getTargetType());
        if (comment.getTargetId() == null) {
            throw new IllegalArgumentException("内容ID不能为空");
        }
        comment.setTargetType(targetType.name());
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
        fillCommentUsers(result.getRecords());
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
        fillCommentUsers(result.getRecords());
        return result;
    }

    /** 统一补充留言人的昵称与头像（列表页与详情页共用同一份展示数据）。 */
    private void fillCommentUsers(List<Comment> comments) {
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
    public boolean deleteComment(Long id, Long operatorId) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new RuntimeException("留言不存在");
        }
        // 归属校验与收藏/点赞的取消操作一致：普通用户只能删除本人留言；
        // 管理员（operatorId 为 null）不受归属限制
        if (operatorId != null && !operatorId.equals(comment.getUserId())) {
            throw new RuntimeException("无权删除他人留言");
        }
        return commentMapper.deleteById(id) > 0;
    }

    // ==================== 收藏 / 点赞：统一委托对应处理器 ====================

    @Override
    public boolean addFavorite(Long userId, String targetType, Long targetId) {
        return favoriteHandler().add(userId, targetType, targetId);
    }

    @Override
    public boolean removeFavorite(Long userId, String targetType, Long targetId) {
        return favoriteHandler().remove(userId, targetType, targetId);
    }

    @Override
    public boolean isFavorited(Long userId, String targetType, Long targetId) {
        return favoriteHandler().isInteracted(userId, targetType, targetId);
    }

    @Override
    public List<Favorite> listUserFavorites(Long userId, String targetType) {
        return favoriteHandler().listByUser(userId, targetType);
    }

    @Override
    public boolean addLike(Long userId, String targetType, Long targetId) {
        return likeHandler().add(userId, targetType, targetId);
    }

    @Override
    public boolean removeLike(Long userId, String targetType, Long targetId) {
        return likeHandler().remove(userId, targetType, targetId);
    }

    @Override
    public boolean isLiked(Long userId, String targetType, Long targetId) {
        return likeHandler().isInteracted(userId, targetType, targetId);
    }

    @Override
    public long countLikes(String targetType, Long targetId) {
        return likeHandler().count(targetType, targetId);
    }
}
