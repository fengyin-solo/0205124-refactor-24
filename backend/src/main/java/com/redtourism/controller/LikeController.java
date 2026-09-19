package com.redtourism.controller;

import com.redtourism.common.Result;
import com.redtourism.service.InteractionKind;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like")
public class LikeController extends AbstractToggleInteractionController {

    @Override
    protected InteractionKind kind() {
        return InteractionKind.LIKE;
    }

    @Override
    protected String addSuccessMessage() {
        return "点赞成功";
    }

    @Override
    protected String removeSuccessMessage() {
        return "取消点赞";
    }

    @GetMapping("/count")
    public Result<Long> count(@RequestParam String targetType,
                               @RequestParam Long targetId) {
        return Result.success(interactionService.countInteractions(kind(), targetType, targetId));
    }
}
