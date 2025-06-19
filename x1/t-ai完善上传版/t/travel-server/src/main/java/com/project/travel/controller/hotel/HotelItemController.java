package com.project.travel.controller.hotel;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.travel.domain.ApiResponse;
import com.project.travel.domain.HotelItem;
import com.project.travel.domain.HotelOrder;
import com.project.travel.enums.ResultCode;
import com.project.travel.service.HotelItemService;
import com.project.travel.service.HotelOrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@ResponseBody
@RequestMapping("item")
public class HotelItemController {

    @Autowired
    private HotelItemService hotelItemService;
    @Autowired
    private HotelOrderService hotelOrderService;

    /** 分页获取房型 */
    @PostMapping("getHotelItemPage")
    public ApiResponse getHotelItemPage(@RequestBody HotelItem hotelItem) {
        Page<HotelItem> page = new Page<>(hotelItem.getPageNumber(), hotelItem.getPageSize());
        QueryWrapper<HotelItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(hotelItem.getHotelId()), HotelItem::getHotelId, hotelItem.getHotelId())
                .like(StringUtils.isNotBlank(hotelItem.getName()), HotelItem::getName, hotelItem.getName());
        Page<HotelItem> sysHotelItemPage = hotelItemService.page(page, queryWrapper);
        return ApiResponse.success(sysHotelItemPage);
    }

    @GetMapping("getHotelItemList")
    public ApiResponse getHotelItemList(@RequestParam("id") String id) {
        QueryWrapper<HotelItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(HotelItem::getHotelId,id);
        List<HotelItem> itemList = hotelItemService.list(queryWrapper);
        return ApiResponse.success(itemList);
    }

    /** 根据id获取房型 */
    @GetMapping("getHotelItemById")
    public ApiResponse getHotelItemById(@RequestParam("id")String id) {
        HotelItem hotelItem = hotelItemService.getById(id);
        return ApiResponse.success(hotelItem);
    }

    /** 保存房型 */
    @PostMapping("saveHotelItem")
    public ApiResponse saveHotelItem(@RequestBody HotelItem hotelItem) {
        boolean save = hotelItemService.save(hotelItem);
        if (save) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑房型 */
    @PostMapping("editHotelItem")
    public ApiResponse editHotelItem(@RequestBody HotelItem hotelItem) {
        boolean save = hotelItemService.updateById(hotelItem);
        if (save) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除房型 */
    @GetMapping("removeHotelItem")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse removeHotelItem(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                hotelItemService.removeById(id);
                QueryWrapper<HotelOrder> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(HotelOrder::getItemId,id);
                hotelOrderService.remove(queryWrapper);
            }
            return ApiResponse.success();
        } else {
            return ApiResponse.fail("房型id不能为空！");
        }
    }

}
