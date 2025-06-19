package com.project.travel.controller.forum;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.travel.domain.ApiResponse;
import com.project.travel.domain.ForumPost;
import com.project.travel.enums.ResultCode;
import com.project.travel.service.ForumService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
@RequestMapping("forum")
public class ForumController {

    @Autowired
    private ForumService forumService;

    /** 分页获取资讯 */
    @PostMapping("getForumPage")
    public ApiResponse getForumPage(@RequestBody ForumPost forumPost) {
        Page<ForumPost> page = new Page<>(forumPost.getPageNumber(), forumPost.getPageSize());
        QueryWrapper<ForumPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(forumPost.getTitle()), ForumPost::getTitle, forumPost.getTitle())
                .orderByDesc(ForumPost::getCreateTime);
        Page<ForumPost> sysForumPage = forumService.page(page, queryWrapper);
        return ApiResponse.success(sysForumPage);
    }

    /** 根据id获取资讯 */
    @GetMapping("getForumById")
    public ApiResponse getForumById(@RequestParam("id")String id) {
        ForumPost forumPost = forumService.getById(id);
        return ApiResponse.success(forumPost);
    }

    /** 保存资讯 */
    @PostMapping("saveForum")
    public ApiResponse saveForum(@RequestBody ForumPost forumPost) {
        boolean save = forumService.save(forumPost);
        if (save) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑资讯 */
    @PostMapping("editForum")
    public ApiResponse editForum(@RequestBody ForumPost forumPost) {
        boolean save = forumService.updateById(forumPost);
        if (save) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除资讯 */
    @GetMapping("removeForum")
    public ApiResponse removeForum(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                forumService.removeById(id);
            }
            return ApiResponse.success();
        } else {
            return ApiResponse.fail("资讯id不能为空！");
        }
    }

}