package com.redtourism.interaction.handler;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.redtourism.entity.LikeRecord;
import com.redtourism.interaction.AbstractUserInteractionHandler;
import com.redtourism.interaction.InteractionType;
import com.redtourism.mapper.LikeRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 点赞的差异部分：实体、Mapper、对应字段；
 * 支持的内容类型（CULTURE）与“已点赞”提示由 {@link InteractionType#LIKE} 声明。
 */
@Component
public class LikeInteractionHandler extends AbstractUserInteractionHandler<LikeRecord> {

    @Autowired
    private LikeRecordMapper likeRecordMapper;

    @Override
    public InteractionType getInteractionType() {
        return InteractionType.LIKE;
    }

    @Override
    protected BaseMapper<LikeRecord> getMapper() {
        return likeRecordMapper;
    }

    @Override
    protected LikeRecord newRecord() {
        return new LikeRecord();
    }

    @Override
    protected SFunction<LikeRecord, Long> userIdColumn() {
        return LikeRecord::getUserId;
    }

    @Override
    protected SFunction<LikeRecord, String> targetTypeColumn() {
        return LikeRecord::getTargetType;
    }

    @Override
    protected SFunction<LikeRecord, Long> targetIdColumn() {
        return LikeRecord::getTargetId;
    }

    @Override
    protected SFunction<LikeRecord, Date> createTimeColumn() {
        return LikeRecord::getCreateTime;
    }
}
