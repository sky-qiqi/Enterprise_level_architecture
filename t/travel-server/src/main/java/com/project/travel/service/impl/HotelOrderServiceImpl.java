package com.project.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.travel.domain.HotelOrder;
import com.project.travel.mapper.HotelOrderMapper;
import com.project.travel.service.HotelOrderService;
import org.springframework.stereotype.Service;

@Service
public class HotelOrderServiceImpl extends ServiceImpl<HotelOrderMapper, HotelOrder> implements HotelOrderService {
}
