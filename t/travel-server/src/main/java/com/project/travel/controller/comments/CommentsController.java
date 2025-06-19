package com.project.travel.controller.comments;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.travel.domain.ApiResponse;
import com.project.travel.domain.Comments;
import com.project.travel.domain.User;
import com.project.travel.enums.ResultCode;
import com.project.travel.service.CommentsService;
import com.project.travel.service.UserService;
import com.project.travel.utils.TokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
@RequestMapping("comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;
    @Autowired
    private UserService userService;

    /** 分页获取评论 */
    @PostMapping("getCommentsPage")
    public ApiResponse getCommentsPage(@RequestBody Comments comments) {
        Page<Comments> page = new Page<>(comments.getPageNumber(), comments.getPageSize());
        QueryWrapper<Comments> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(comments.getContent()), Comments::getContent, comments.getContent())
                .eq(StringUtils.isNotBlank(comments.getAttractionsId()), Comments::getAttractionsId, comments.getAttractionsId())
                .eq(StringUtils.isNotBlank(comments.getUserId()), Comments::getUserId, comments.getUserId())
                .eq(StringUtils.isNotBlank(comments.getAvatar()), Comments::getAvatar, comments.getAvatar())
                .eq(StringUtils.isNotBlank(comments.getCreateBy()), Comments::getCreateBy, comments.getCreateBy())
                .eq(comments.getCreateTime() != null, Comments::getCreateTime, comments.getCreateTime())
                .eq(StringUtils.isNotBlank(comments.getUpdateBy()), Comments::getUpdateBy, comments.getUpdateBy())
                .eq(comments.getUpdateTime() != null, Comments::getUpdateTime, comments.getUpdateTime());
        Page<Comments> sysCommentsPage = commentsService.page(page, queryWrapper);
        return ApiResponse.success(sysCommentsPage);
    }

    /** 根据id获取评论 */
    @GetMapping("getCommentsById")
    public ApiResponse getCommentsById(@RequestParam("id")String id) {
        Comments comments = commentsService.getById(id);
        return ApiResponse.success(comments);
    }

    /** 保存评论 */
    @PostMapping("saveComments")
    public ApiResponse saveComments(@RequestBody Comments comments) {
        User user = userService.getById(TokenUtils.getUserIdByToken());
        comments.setAvatar(user.getAvatar());
        boolean save = commentsService.save(comments);
        if (save) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑评论 */
    @PostMapping("editComments")
    public ApiResponse editComments(@RequestBody Comments comments) {
        boolean save = commentsService.updateById(comments);
        if (save) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除评论 */
    @GetMapping("removeComments")
    public ApiResponse removeComments(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                commentsService.removeById(id);
            }
            return ApiResponse.success();
        } else {
            return ApiResponse.fail("评论id不能为空！");
        }
    }

}