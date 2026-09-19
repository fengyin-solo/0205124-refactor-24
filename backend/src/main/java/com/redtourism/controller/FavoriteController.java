package com.redtourism.controller;

import com.redtourism.common.Constants;
import com.redtourism.common.Result;
import com.redtourism.entity.Favorite;
import com.redtourism.entity.User;
import com.redtourism.service.InteractionKind;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController extends AbstractToggleInteractionController {

    @Override
    protected InteractionKind kind() {
        return InteractionKind.FAVORITE;
    }

    @Override
    protected String addSuccessMessage() {
        return "收藏成功";
    }

    @Override
    protected String removeSuccessMessage() {
        return "取消收藏";
    }

    @GetMapping("/myList")
    public Result<List<Favorite>> myList(@RequestParam(required = false) String targetType,
                                          HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        if (user == null) return Result.error(401, "请先登录");
        List<Favorite> favorites = interactionService.listUserInteractions(kind(), user.getId(), targetType);
        return Result.success(favorites);
    }
}
