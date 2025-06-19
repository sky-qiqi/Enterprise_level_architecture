package com.project.travel.controller.rotations;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.travel.domain.ApiResponse;
import com.project.travel.domain.Carousel;
import com.project.travel.enums.ResultCode;
import com.project.travel.service.CarouselService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("rotations")
public class RotationsController {

    @Autowired
    private CarouselService carouselService;

    /** 分页获取轮播图 */
    @PostMapping("getRotationsPage")
    public ApiResponse getRotationsPage(@RequestBody Carousel carousel) {
        Page<Carousel> page = new Page<>(carousel.getPageNumber(), carousel.getPageSize());
        QueryWrapper<Carousel> queryWrapper = new QueryWrapper<>();
        Page<Carousel> sysRotationsPage = carouselService.page(page, queryWrapper);
        return ApiResponse.success(sysRotationsPage);
    }

    @GetMapping("getRotationsList")
    public ApiResponse getRotationsList() {
        List<Carousel> carouselList = carouselService.list();
        return ApiResponse.success(carouselList);
    }

    /** 根据id获取轮播图 */
    @GetMapping("getRotationsById")
    public ApiResponse getRotationsById(@RequestParam("id")String id) {
        Carousel carousel = carouselService.getById(id);
        return ApiResponse.success(carousel);
    }

    /** 保存轮播图 */
    @PostMapping("saveRotations")
    public ApiResponse saveRotations(@RequestBody Carousel carousel) {
        boolean save = carouselService.save(carousel);
        if (save) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑轮播图 */
    @PostMapping("editRotations")
    public ApiResponse editRotations(@RequestBody Carousel carousel) {
        boolean save = carouselService.updateById(carousel);
        if (save) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除轮播图 */
    @GetMapping("removeRotations")
    public ApiResponse removeRotations(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                carouselService.removeById(id);
            }
            return ApiResponse.success();
        } else {
            return ApiResponse.fail("轮播图id不能为空！");
        }
    }

}
