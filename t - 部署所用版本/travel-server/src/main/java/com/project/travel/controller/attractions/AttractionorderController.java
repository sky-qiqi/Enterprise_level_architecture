package com.project.travel.controller.attractions;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.travel.domain.ApiResponse;
import com.project.travel.domain.AttractionOrder;
import com.project.travel.domain.Attractions;
import com.project.travel.domain.Comments;
import com.project.travel.domain.Favorite;
import com.project.travel.enums.ResultCode;
import com.project.travel.service.AttractionsOrderService;
import com.project.travel.service.AttractionsService;
import com.project.travel.service.CommentsService;
import com.project.travel.service.FavoriteService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Controller
@ResponseBody
@RequestMapping("attractions")
public class AttractionorderController {

    @Autowired
    private AttractionsService attractionsService;
    @Autowired
    private AttractionsOrderService attractionsOrderService;
    @Autowired
    private CommentsService commentsService;
    @Autowired
    private FavoriteService favoriteService;

    /** 分页获取景点 */
    @PostMapping("getAttractionsPage")
    public ApiResponse getAttractionsPage(@RequestBody Attractions attractions) {
        Page<Attractions> page = new Page<>(attractions.getPageNumber(), attractions.getPageSize());
        QueryWrapper<Attractions> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(attractions.getState() != null, Attractions::getState, attractions.getState())
                .like(StringUtils.isNotBlank(attractions.getName()), Attractions::getName, attractions.getName())
                .orderByDesc(Attractions::getCreateTime);
        Page<Attractions> sysAttractionsPage = attractionsService.page(page, queryWrapper);
        return ApiResponse.success(sysAttractionsPage);
    }

    @GetMapping("getAttractionsList")
    public ApiResponse getAttractionsList() {
        List<Attractions> attractionsList = attractionsService.list();
        return ApiResponse.success(attractionsList);
    }

    @GetMapping("getAttractionsIndex")
    public ApiResponse getAttractionsIndex() {
        QueryWrapper<Attractions> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Attractions::getState,1).last("limit 3");
        List<Attractions> attractionsList = attractionsService.list(queryWrapper);
        return ApiResponse.success(attractionsList);
    }

    /** 根据id获取景点 */
    @GetMapping("getAttractionsById")
    public ApiResponse getAttractionsById(@RequestParam("id")String id) {
        Attractions attractions = attractionsService.getById(id);
        return ApiResponse.success(attractions);
    }

    /** 保存景点 */
    @PostMapping("saveAttractions")
    public ApiResponse saveAttractions(@RequestBody Attractions attractions) {
        boolean save = attractionsService.save(attractions);
        if (save) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑景点 */
    @PostMapping("editAttractions")
    public ApiResponse editAttractions(@RequestBody Attractions attractions) {
        boolean save = attractionsService.updateById(attractions);
        if (save) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除景点 */
    @GetMapping("removeAttractions")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse removeAttractions(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                attractionsService.removeById(id);
                QueryWrapper<AttractionOrder> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(AttractionOrder::getAttractionsId,id);
                attractionsOrderService.remove(queryWrapper);
                QueryWrapper<Comments> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(Comments::getAttractionsId,id);
                commentsService.remove(wrapper);
            }
            return ApiResponse.success();
        } else {
            return ApiResponse.fail("景点id不能为空！");
        }
    }

    /**
     * 景点排行榜接口：根据收藏数量和预约数量综合排行
     */
    @GetMapping("rank")
    public ApiResponse rank() {
        // 统计每个景点的收藏数
        List<Favorite> allFavorites = favoriteService.list();
        Map<String, Long> favorCount = allFavorites.stream()
                .collect(Collectors.groupingBy(Favorite::getLineId, Collectors.counting()));
        // 统计每个景点的预约数
        List<AttractionOrder> allOrders = attractionsOrderService.list();
        Map<String, Long> orderCount = allOrders.stream()
                .collect(Collectors.groupingBy(AttractionOrder::getAttractionsId, Collectors.counting()));
        // 合并所有景点id
        Set<String> allAttractionIds = new java.util.HashSet<>();
        allAttractionIds.addAll(favorCount.keySet());
        allAttractionIds.addAll(orderCount.keySet());
        // 综合排序：收藏数*2+预约数，降序
        List<String> sortedAttractionIds = allAttractionIds.stream()
                .sorted((a, b) -> {
                    long scoreA = favorCount.getOrDefault(a, 0L) * 2 + orderCount.getOrDefault(a, 0L);
                    long scoreB = favorCount.getOrDefault(b, 0L) * 2 + orderCount.getOrDefault(b, 0L);
                    return Long.compare(scoreB, scoreA);
                })
                .collect(Collectors.toList());
        // 查询前10条景点详情
        List<Attractions> rankAttractions = new ArrayList<>();
        for (String id : sortedAttractionIds) {
            Attractions attractions = attractionsService.getById(id);
            if (attractions != null) {
                rankAttractions.add(attractions);
            }
            if (rankAttractions.size() >= 10) break;
        }
        return ApiResponse.success(rankAttractions);
    }

}
