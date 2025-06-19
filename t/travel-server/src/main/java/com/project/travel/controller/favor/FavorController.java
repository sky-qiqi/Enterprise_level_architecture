package com.project.travel.controller.favor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.travel.domain.ApiResponse;
import com.project.travel.domain.Favorite;
import com.project.travel.domain.TravelRoute;
import com.project.travel.domain.AttractionOrder;
import com.project.travel.enums.ResultCode;
import com.project.travel.service.FavoriteService;
import com.project.travel.service.TravelRouteService;
import com.project.travel.service.AttractionsOrderService;
import com.project.travel.utils.TokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.project.travel.mapper.FavorMapper;
import com.project.travel.mapper.AttractionOrdermapper;

@Controller
@ResponseBody
@RequestMapping("favor")
public class FavorController {

    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private TravelRouteService travelRouteService;
    @Autowired
    private AttractionsOrderService attractionsOrderService;
    @Autowired
    private FavorMapper favorMapper;
    @Autowired
    private AttractionOrdermapper attractionOrdermapper;

    /** 分页获取收藏 */
    @PostMapping("getFavorPage")
    public ApiResponse getFavorPage(@RequestBody Favorite favorite) {
        Page<Favorite> page = new Page<>(favorite.getPageNumber(), favorite.getPageSize());
        QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(favorite.getLineId()), Favorite::getLineId, favorite.getLineId())
                .eq(StringUtils.isNotBlank(favorite.getName()), Favorite::getName, favorite.getName())
                .eq(StringUtils.isNotBlank(favorite.getIntroduce()), Favorite::getIntroduce, favorite.getIntroduce())
                .eq(StringUtils.isNotBlank(favorite.getImages()), Favorite::getImages, favorite.getImages())
                .eq(StringUtils.isNotBlank(favorite.getUserId()), Favorite::getUserId, favorite.getUserId());
        Page<Favorite> sysFavorPage = favoriteService.page(page, queryWrapper);
        return ApiResponse.success(sysFavorPage);
    }

    @GetMapping("getFavor")
    public ApiResponse getFavor(@RequestParam("id")String id) {
        String userIdByToken = TokenUtils.getUserIdByToken();
        QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Favorite::getUserId,userIdByToken)
                .eq(Favorite::getLineId,id).last("limit 1");
        Favorite favorite = favoriteService.getOne(queryWrapper);
        if (favorite != null) {
            return ApiResponse.success(favorite);
        } else {
            return ApiResponse.fail();
        }
    }

    /** 根据id获取收藏 */
    @GetMapping("getFavorById")
    public ApiResponse getFavorById(@RequestParam("id")String id) {
        Favorite favorite = favoriteService.getById(id);
        return ApiResponse.success(favorite);
    }

    /** 保存收藏 */
    @PostMapping("saveFavor")
    public ApiResponse saveFavor(@RequestBody Favorite favorite) {
        favorite.setUserId(TokenUtils.getUserIdByToken());
        TravelRoute travelRoute = travelRouteService.getById(favorite.getLineId());
        favorite.setName(travelRoute.getName());
        favorite.setIntroduce(travelRoute.getIntroduce());
        favorite.setImages(travelRoute.getImages());
        boolean save = favoriteService.save(favorite);
        if (save) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑收藏 */
    @PostMapping("editFavor")
    public ApiResponse editFavor(@RequestBody Favorite favorite) {
        boolean save = favoriteService.updateById(favorite);
        if (save) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除收藏 */
    @GetMapping("removeFavor")
    public ApiResponse removeFavor(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                favoriteService.removeById(id);
            }
            return ApiResponse.success();
        } else {
            return ApiResponse.fail("收藏id不能为空！");
        }
    }

    /**
     * 旅游推荐榜接口：根据收藏数量和预约数量综合排行（实时SQL聚合）
     */
    @GetMapping("recommend/rank")
    public ApiResponse recommendRank() {
        // 统计收藏数（favor表）
        List<Map<String, Object>> favorList = favorMapper.countFavorGroupByLineId();
        Map<String, Long> favorCount = favorList.stream().collect(Collectors.toMap(
                m -> String.valueOf(m.get("line_id")), m -> ((Number) m.get("favor_count")).longValue()));

        // 统计预定数（attraction_order表，按line_id分组）
        List<Map<String, Object>> orderList = attractionOrdermapper.countOrderGroupByLineId();
        Map<String, Long> orderCount = orderList.stream().collect(Collectors.toMap(
                m -> String.valueOf(m.get("line_id")), m -> ((Number) m.get("order_count")).longValue()));

        // 合并所有路线id
        Set<String> allLineIds = new java.util.HashSet<>();
        allLineIds.addAll(favorCount.keySet());
        allLineIds.addAll(orderCount.keySet());

        // 综合排序
        List<String> sortedLineIds = allLineIds.stream()
                .sorted((a, b) -> {
                    long scoreA = favorCount.getOrDefault(a, 0L) * 2 + orderCount.getOrDefault(a, 0L);
                    long scoreB = favorCount.getOrDefault(b, 0L) * 2 + orderCount.getOrDefault(b, 0L);
                    return Long.compare(scoreB, scoreA);
                })
                .collect(Collectors.toList());

        // 查询前10条路线详情，并附加收藏量和预定量
        List<Map<String, Object>> rankRoutes = new ArrayList<>();
        for (String lineId : sortedLineIds) {
            TravelRoute route = travelRouteService.getById(lineId);
            if (route != null) {
                Map<String, Object> map = new java.util.HashMap<>();
                map.put("id", route.getId());
                map.put("name", route.getName());
                map.put("introduce", route.getIntroduce());
                map.put("images", route.getImages());
                map.put("favorCount", favorCount.getOrDefault(lineId, 0L));
                map.put("orderCount", orderCount.getOrDefault(lineId, 0L));
                rankRoutes.add(map);
            }
            if (rankRoutes.size() >= 10) break;
        }
        return ApiResponse.success(rankRoutes);
    }

    /**
     * 猜你喜欢接口：每次随机顺序返回推荐列表
     */
    @GetMapping("recommend")
    public ApiResponse recommend() {
        String userId = TokenUtils.getUserIdByToken();
        // 查询当前用户已收藏的路线id
        List<Favorite> userFavorites = favoriteService.list(new QueryWrapper<Favorite>().lambda().eq(Favorite::getUserId, userId));
        Set<String> userLineIds = userFavorites.stream().map(Favorite::getLineId).collect(Collectors.toSet());
        // 查询所有收藏，统计每条路线被收藏次数
        List<Favorite> allFavorites = favoriteService.list();
        Map<String, Long> lineIdCount = allFavorites.stream()
                .collect(Collectors.groupingBy(Favorite::getLineId, Collectors.counting()));
        // 查询当前用户下过订单的景点id
        List<AttractionOrder> userOrders = attractionsOrderService.list(new QueryWrapper<AttractionOrder>().lambda().eq(AttractionOrder::getUserId, userId));
        Set<String> userOrderAttractionIds = userOrders.stream().map(AttractionOrder::getAttractionsId).collect(Collectors.toSet());
        // 统计所有用户订单的景点id
        List<AttractionOrder> allOrders = attractionsOrderService.list();
        Map<String, Long> attractionOrderCount = allOrders.stream()
                .collect(Collectors.groupingBy(AttractionOrder::getAttractionsId, Collectors.counting()));
        // 推荐逻辑
        List<String> recommendByOrder = userOrderAttractionIds.stream()
                .filter(id -> !userLineIds.contains(id))
                .collect(Collectors.toList());
        List<String> recommendByOrderHot = attractionOrderCount.entrySet().stream()
                .filter(e -> !userLineIds.contains(e.getKey()) && !recommendByOrder.contains(e.getKey()))
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        List<String> recommendByFavorHot = lineIdCount.entrySet().stream()
                .filter(e -> !userLineIds.contains(e.getKey()) && !recommendByOrder.contains(e.getKey()) && !recommendByOrderHot.contains(e.getKey()))
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        List<String> recommendLineIds = new ArrayList<>();
        recommendLineIds.addAll(recommendByOrder);
        recommendLineIds.addAll(recommendByOrderHot);
        recommendLineIds.addAll(recommendByFavorHot);
        // 打乱顺序
        java.util.Collections.shuffle(recommendLineIds);
        // 查询推荐的路线详情，最多推荐5条
        List<TravelRoute> recommendRoutes = new ArrayList<>();
        for (String lineId : recommendLineIds) {
            TravelRoute route = travelRouteService.getById(lineId);
            if (route != null) {
                recommendRoutes.add(route);
            }
            if (recommendRoutes.size() >= 5) break;
        }
        return ApiResponse.success(recommendRoutes);
    }
}