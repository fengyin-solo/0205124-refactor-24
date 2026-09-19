package com.redtourism.interaction.handler;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.redtourism.entity.Favorite;
import com.redtourism.interaction.AbstractUserInteractionHandler;
import com.redtourism.interaction.InteractionType;
import com.redtourism.mapper.FavoriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 收藏的差异部分：实体、Mapper、对应字段；
 * 支持的内容类型（SPOT/ROUTE/CULTURE）与“已收藏”提示由 {@link InteractionType#FAVORITE} 声明。
 */
@Component
public class FavoriteInteractionHandler extends AbstractUserInteractionHandler<Favorite> {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public InteractionType getInteractionType() {
        return InteractionType.FAVORITE;
    }

    @Override
    protected BaseMapper<Favorite> getMapper() {
        return favoriteMapper;
    }

    @Override
    protected Favorite newRecord() {
        return new Favorite();
    }

    @Override
    protected SFunction<Favorite, Long> userIdColumn() {
        return Favorite::getUserId;
    }

    @Override
    protected SFunction<Favorite, String> targetTypeColumn() {
        return Favorite::getTargetType;
    }

    @Override
    protected SFunction<Favorite, Long> targetIdColumn() {
        return Favorite::getTargetId;
    }

    @Override
    protected SFunction<Favorite, Date> createTimeColumn() {
        return Favorite::getCreateTime;
    }
}
