package com.project.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.travel.domain.Favorite;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

public interface FavorMapper extends BaseMapper<Favorite> {
    /**
     * 统计每条路线的收藏数
     */
    @Select("SELECT line_id, COUNT(*) AS favor_count FROM favor GROUP BY line_id")
    List<Map<String, Object>> countFavorGroupByLineId();
}
