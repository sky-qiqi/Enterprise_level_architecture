package com.project.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.travel.domain.AttractionOrder;
import com.project.travel.domain.Attractions;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface AttractionOrdermapper extends BaseMapper<AttractionOrder> {
    /**
     * 统计每条路线的预定数（以景点id为主）
     */
    @Select("SELECT attractions_id, COUNT(*) AS order_count FROM attraction_order GROUP BY attractions_id")
    List<Map<String, Object>> countOrderGroupByAttractionsId();

    /**
     * 统计每条路线的预定数（按line_id分组）
     */
    @Select("SELECT line_id, COUNT(*) AS order_count FROM attraction_order WHERE line_id IS NOT NULL GROUP BY line_id")
    List<Map<String, Object>> countOrderGroupByLineId();

    @Select("SELECT * FROM attractions ORDER BY create_time DESC LIMIT 10")
    List<Attractions> getNewAttractions();

    @Select("SELECT * FROM attractions WHERE FIND_IN_SET(#{tag}, tags) AND id != #{id} LIMIT 5")
    List<Attractions> getSimilarAttractions(@Param("tag") String tag, @Param("id") String id);

    @Select("SELECT * FROM attractions ORDER BY RAND() LIMIT 5")
    List<Attractions> getRandomAttractions();

    @Select("SELECT a.* FROM attractions a " +
            "JOIN favor f ON a.id = f.line_id " +
            "WHERE f.user_id IN (SELECT user_id FROM favor WHERE line_id = #{id}) " +
            "AND a.id != #{id} GROUP BY a.id ORDER BY COUNT(*) DESC LIMIT 5")
    List<Attractions> getAlsoLikedAttractions(@Param("id") String id);
}
