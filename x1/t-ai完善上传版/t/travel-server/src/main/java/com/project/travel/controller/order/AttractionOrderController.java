package com.project.travel.controller.order;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.travel.domain.ApiResponse;
import com.project.travel.domain.AttractionOrder;
import com.project.travel.domain.Attractions;
import com.project.travel.domain.User;
import com.project.travel.enums.ResultCode;
import com.project.travel.service.AttractionsOrderService;
import com.project.travel.service.AttractionsService;
import com.project.travel.service.UserService;
import com.project.travel.utils.TokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
@RequestMapping("order")
public class AttractionOrderController {

    @Autowired
    private AttractionsOrderService attractionsOrderService;
    @Autowired
    private UserService userService;
    @Autowired
    private AttractionsService attractionsService;

    /** 分页获取景点预约 */
    @PostMapping("getAttractionOrderPage")
    public ApiResponse getAttractionOrderPage(@RequestBody AttractionOrder attractionOrder) {
        Page<AttractionOrder> page = new Page<>(attractionOrder.getPageNumber(), attractionOrder.getPageSize());
        QueryWrapper<AttractionOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(attractionOrder.getAttractionsId()), AttractionOrder::getAttractionsId, attractionOrder.getAttractionsId())
                .like(StringUtils.isNotBlank(attractionOrder.getName()), AttractionOrder::getName, attractionOrder.getName())
                .eq(StringUtils.isNotBlank(attractionOrder.getIntroduce()), AttractionOrder::getIntroduce, attractionOrder.getIntroduce())
                .eq(StringUtils.isNotBlank(attractionOrder.getImages()), AttractionOrder::getImages, attractionOrder.getImages())
                .eq(attractionOrder.getNum() != null, AttractionOrder::getNum, attractionOrder.getNum())
                .eq(attractionOrder.getTime() != null, AttractionOrder::getTime, attractionOrder.getTime())
                .eq(StringUtils.isNotBlank(attractionOrder.getPeople()), AttractionOrder::getPeople, attractionOrder.getPeople())
                .eq(attractionOrder.getState() != null, AttractionOrder::getState, attractionOrder.getState())
                .eq(StringUtils.isNotBlank(attractionOrder.getUserId()), AttractionOrder::getUserId, attractionOrder.getUserId())
                .like(StringUtils.isNotBlank(attractionOrder.getCreateBy()), AttractionOrder::getCreateBy, attractionOrder.getCreateBy())
                .eq(attractionOrder.getCreateTime() != null, AttractionOrder::getCreateTime, attractionOrder.getCreateTime())
                .eq(StringUtils.isNotBlank(attractionOrder.getUpdateBy()), AttractionOrder::getUpdateBy, attractionOrder.getUpdateBy())
                .eq(attractionOrder.getUpdateTime() != null, AttractionOrder::getUpdateTime, attractionOrder.getUpdateTime());
        Page<AttractionOrder> sysAttractionOrderPage = attractionsOrderService.page(page, queryWrapper);
        return ApiResponse.success(sysAttractionOrderPage);
    }

    /** 根据id获取景点预约 */
    @GetMapping("getAttractionOrderById")
    public ApiResponse getAttractionOrderById(@RequestParam("id")String id) {
        AttractionOrder attractionOrder = attractionsOrderService.getById(id);
        return ApiResponse.success(attractionOrder);
    }

    /** 保存景点预约 */
    @PostMapping("saveAttractionOrder")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse saveAttractionOrder(@RequestBody AttractionOrder attractionOrder) {
        String userId = TokenUtils.getUserIdByToken();
        User user = userService.getById(userId);
        attractionOrder.setUserId(user.getId());
        Attractions attractions = attractionsService.getById(attractionOrder.getAttractionsId());
        if (attractions.getNum() - attractionOrder.getNum() < 0) {
            return ApiResponse.fail("库存不足");
        }
        attractions.setNum(attractions.getNum() - attractionOrder.getNum());
        attractionsService.updateById(attractions);
        attractionOrder.setName(attractions.getName());
        attractionOrder.setIntroduce(attractions.getIntroduce());
        attractionOrder.setImages(attractions.getImages());
        boolean save = attractionsOrderService.save(attractionOrder);
        if (save) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑景点预约 */
    @PostMapping("editAttractionOrder")
    public ApiResponse editAttractionOrder(@RequestBody AttractionOrder attractionOrder) {
        boolean save = attractionsOrderService.updateById(attractionOrder);
        if (save) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除景点预约 */
    @GetMapping("removeAttractionOrder")
    public ApiResponse removeAttractionOrder(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                attractionsOrderService.removeById(id);
            }
            return ApiResponse.success();
        } else {
            return ApiResponse.fail("景点预约id不能为空！");
        }
    }

}