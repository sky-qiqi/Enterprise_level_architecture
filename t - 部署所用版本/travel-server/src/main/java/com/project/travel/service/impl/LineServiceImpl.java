package com.project.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.travel.domain.TravelRoute;
import com.project.travel.mapper.LineMapper;
import com.project.travel.service.TravelRouteService;
import org.springframework.stereotype.Service;

@Service
public class LineServiceImpl extends ServiceImpl<LineMapper, TravelRoute> implements TravelRouteService {
}
