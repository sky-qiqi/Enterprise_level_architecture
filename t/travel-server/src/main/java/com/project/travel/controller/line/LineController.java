package com.project.travel.controller.line;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.travel.domain.ApiResponse;
import com.project.travel.domain.Favorite;
import com.project.travel.domain.TravelRoute;
import com.project.travel.enums.ResultCode;
import com.project.travel.service.FavoriteService;
import com.project.travel.service.TravelRouteService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("line")
public class LineController {

    @Autowired
    private TravelRouteService travelRouteService;
    @Autowired
    private FavoriteService favoriteService;

    /** 分页获取旅游线路 */
    @PostMapping("getLinePage")
    public ApiResponse getLinePage(@RequestBody TravelRoute travelRoute) {
        Page<TravelRoute> page = new Page<>(travelRoute.getPageNumber(), travelRoute.getPageSize());
        QueryWrapper<TravelRoute> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .like(StringUtils.isNotBlank(travelRoute.getName()), TravelRoute::getName, travelRoute.getName())
                .like(StringUtils.isNotBlank(travelRoute.getGeography()), TravelRoute::getGeography, travelRoute.getGeography())
                .eq(travelRoute.getUpdateTime() != null, TravelRoute::getUpdateTime, travelRoute.getUpdateTime())
                .eq(travelRoute.getTemperature() != null, TravelRoute::getTemperature, travelRoute.getTemperature())
                .orderByDesc(TravelRoute::getCreateTime);
        Page<TravelRoute> sysLinePage = travelRouteService.page(page, queryWrapper);
        return ApiResponse.success(sysLinePage);
    }

    @GetMapping("/getLineIndex")
    public ApiResponse getLineIndex() {
        QueryWrapper<TravelRoute> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(TravelRoute::getCreateTime).last("limit 3");
        List<TravelRoute> travelRouteList = travelRouteService.list(queryWrapper);
        return ApiResponse.success(travelRouteList);
    }

    /** 根据id获取旅游线路 */
    @GetMapping("getLineById")
    public ApiResponse getLineById(@RequestParam("id")String id) {
        TravelRoute travelRoute = travelRouteService.getById(id);
        return ApiResponse.success(travelRoute);
    }

    /** 保存旅游线路 */
    @PostMapping("saveLine")
    public ApiResponse saveLine(@RequestBody TravelRoute travelRoute) {
        boolean save = travelRouteService.save(travelRoute);
        if (save) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 编辑旅游线路 */
    @PostMapping("editLine")
    public ApiResponse editLine(@RequestBody TravelRoute travelRoute) {
        boolean save = travelRouteService.updateById(travelRoute);
        if (save) {
            return ApiResponse.success();
        } else {
            return ApiResponse.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 删除旅游线路 */
    @GetMapping("removeLine")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse removeLine(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                travelRouteService.removeById(id);
                QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(Favorite::getLineId,id);
                favoriteService.remove(queryWrapper);
            }
            return ApiResponse.success();
        } else {
            return ApiResponse.fail("旅游线路id不能为空！");
        }
    }

}
