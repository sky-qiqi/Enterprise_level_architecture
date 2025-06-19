package com.project.travel.controller.hotel;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.travel.domain.*;
import com.project.travel.enums.ResultCode;
import com.project.travel.service.AttractionsService;
import com.project.travel.service.HotelItemService;
import com.project.travel.service.HotelOrderService;
import com.project.travel.service.HotelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@Controller
@ResponseBody
@RequestMapping("hotel")
public class HotelController {

    @Autowired
    private HotelService hotelService;
    @Autowired
    private AttractionsService attractionsService;
    @Autowired
    private HotelItemService hotelItemService;
    @Autowired
    private HotelOrderService hotelOrderService;

    /** 分页获取酒店 */
    @PostMapping("getHotelPage")
    public ApiResponse getHotelPage(@RequestBody Hotel hotel) {
        Page<Hotel> page = new Page<>(hotel.getPageNumber(), hotel.getPageSize());
        QueryWrapper<Hotel> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(hotel.getAttractions()), Hotel::getAttractions, hotel.getAttractions())
                .eq(hotel.getState() != null, Hotel::getState, hotel.getState())
                .like(StringUtils.isNotBlank(hotel.getName()), Hotel::getName, hotel.getName())
                .orderByDesc(Hotel::getCreateTime);
        Page<Hotel> sysHotelPage = hotelService.page(page, queryWrapper);
        return ApiResponse.success(sysHotelPage);
    }

    /** 根据id获取酒店 */
    @GetMapping("getHotelById")
    public ApiResponse geHotelById(@RequestParam("id")String id) {
        Hotel hotel = hotelService.getById(id);
        return ApiResponse.success(hotel);
    }

    /** 保存酒店 */
    @PostMapping("saveHotel")
    public ApiResponse saveHotel(@RequestBody Hotel hotel) {
        Attractions attractions = attractionsService.getById(hotel.getAttractionsId());
        hotel.setAttractions(attractions.getName());
        boolean save = hotelService.save(hotel);
        if (save) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑酒店 */
    @PostMapping("editHotel")
    public ApiResponse editHotel(@RequestBody Hotel hotel) {
        Attractions attractions = attractionsService.getById(hotel.getAttractionsId());
        hotel.setAttractions(attractions.getName());
        boolean save = hotelService.updateById(hotel);
        if (save) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除酒店 */
    @GetMapping("removeHotel")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse removeHotel(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                hotelService.removeById(id);
                QueryWrapper<HotelItem> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(HotelItem::getHotelId,id);
                hotelItemService.remove(queryWrapper);
                QueryWrapper<HotelOrder> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(HotelOrder::getHotelId,id);
                hotelOrderService.remove(wrapper);
            }
            return ApiResponse.success();
        } else {
            return ApiResponse.fail("酒店id不能为空！");
        }
    }

}