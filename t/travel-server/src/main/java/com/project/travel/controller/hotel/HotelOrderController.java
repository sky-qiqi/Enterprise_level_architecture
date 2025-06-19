package com.project.travel.controller.hotel;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.travel.domain.ApiResponse;
import com.project.travel.domain.Hotel;
import com.project.travel.domain.HotelItem;
import com.project.travel.domain.HotelOrder;
import com.project.travel.enums.ResultCode;
import com.project.travel.service.HotelItemService;
import com.project.travel.service.HotelOrderService;
import com.project.travel.service.HotelService;
import com.project.travel.utils.TokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
@RequestMapping("order")
public class HotelOrderController {

    @Autowired
    private HotelOrderService hotelOrderService;
    @Autowired
    private HotelService hotelService;
    @Autowired
    private HotelItemService hotelItemService;

    /** 分页获取酒店预约 */
    @PostMapping("getHotelOrderPage")
    public ApiResponse getHotelOrderPage(@RequestBody HotelOrder hotelOrder) {
        Page<HotelOrder> page = new Page<>(hotelOrder.getPageNumber(), hotelOrder.getPageSize());
        QueryWrapper<HotelOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(hotelOrder.getHotelId()), HotelOrder::getHotelId, hotelOrder.getHotelId())
                .like(StringUtils.isNotBlank(hotelOrder.getName()), HotelOrder::getName, hotelOrder.getName())
                .eq(hotelOrder.getState() != null, HotelOrder::getState, hotelOrder.getState())
                .eq(StringUtils.isNotBlank(hotelOrder.getUserId()), HotelOrder::getUserId, hotelOrder.getUserId())
                .eq(StringUtils.isNotBlank(hotelOrder.getItemId()), HotelOrder::getItemId, hotelOrder.getItemId())
                .like(StringUtils.isNotBlank(hotelOrder.getCreateBy()), HotelOrder::getCreateBy, hotelOrder.getCreateBy())
                .eq(hotelOrder.getCreateTime() != null, HotelOrder::getCreateTime, hotelOrder.getCreateTime());
        Page<HotelOrder> sysHotelOrderPage = hotelOrderService.page(page, queryWrapper);
        return ApiResponse.success(sysHotelOrderPage);
    }

    /** 根据id获取酒店预约 */
    @GetMapping("getHotelOrderById")
    public ApiResponse getHotelOrderById(@RequestParam("id")String id) {
        HotelOrder hotelOrder = hotelOrderService.getById(id);
        return ApiResponse.success(hotelOrder);
    }

    /** 保存酒店预约 */
    @PostMapping("saveHotelOrder")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse saveHotelOrder(@RequestBody HotelOrder hotelOrder) {
        Hotel hotel = hotelService.getById(hotelOrder.getHotelId());
        hotelOrder.setName(hotel.getName());
        hotelOrder.setIntroduce(hotel.getIntroduce());
        hotelOrder.setImages(hotel.getImages());
        HotelItem item = hotelItemService.getById(hotelOrder.getItemId());
        if (item.getNum() - 1 < 0) {
            return ApiResponse.fail("库存不足");
        }
        item.setNum(item.getNum() - 1);
        hotelItemService.updateById(item);
        hotelOrder.setItemName(item.getName());
        hotelOrder.setPrice(item.getPrice());
        hotelOrder.setUserId(TokenUtils.getUserIdByToken());
        boolean save = hotelOrderService.save(hotelOrder);
        if (save) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑酒店预约 */
    @PostMapping("editHotelOrder")
    public ApiResponse editHotelOrder(@RequestBody HotelOrder hotelOrder) {
        boolean save = hotelOrderService.updateById(hotelOrder);
        if (save) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除酒店预约 */
    @GetMapping("removeHotelOrder")
    public ApiResponse removeHotelOrder(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                hotelOrderService.removeById(id);
            }
            return ApiResponse.success();
        } else {
            return ApiResponse.fail("酒店预约id不能为空！");
        }
    }

}
