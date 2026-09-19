package com.redtourism.controller;

import com.redtourism.common.Constants;
import com.redtourism.common.Result;
import com.redtourism.entity.User;
import com.redtourism.service.InteractionKind;
import com.redtourism.service.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * 开关型互动（收藏、点赞）的共用接口处理。
 * 子类只需声明互动类型与提示文案，即拥有 add/remove/check 一套端点。
 */
public abstract class AbstractToggleInteractionController {

    @Autowired
    protected InteractionService interactionService;

    /** 当前控制器处理的互动类型 */
    protected abstract InteractionKind kind();

    /** 新增成功提示语 */
    protected abstract String addSuccessMessage();

    /** 取消成功提示语 */
    protected abstract String removeSuccessMessage();

    @GetMapping("/add")
    public Result<String> add(@RequestParam String targetType,
                              @RequestParam Long targetId,
                              HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        if (user == null) return Result.error(401, "请先登录");
        interactionService.addInteraction(kind(), user.getId(), targetType, targetId);
        return Result.success(addSuccessMessage(), null);
    }

    @GetMapping("/remove")
    public Result<String> remove(@RequestParam String targetType,
                                 @RequestParam Long targetId,
                                 HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        if (user == null) return Result.error(401, "请先登录");
        interactionService.removeInteraction(kind(), user.getId(), targetType, targetId);
        return Result.success(removeSuccessMessage(), null);
    }

    @GetMapping("/check")
    public Result<Boolean> check(@RequestParam String targetType,
                                 @RequestParam Long targetId,
                                 HttpSession session) {
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        if (user == null) return Result.success(false);
        return Result.success(interactionService.hasInteraction(kind(), user.getId(), targetType, targetId));
    }
}
