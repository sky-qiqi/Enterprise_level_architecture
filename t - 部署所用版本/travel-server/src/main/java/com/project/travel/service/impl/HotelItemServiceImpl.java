package com.project.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.travel.domain.HotelItem;
import com.project.travel.mapper.HotelItemMapper;
import com.project.travel.service.HotelItemService;
import org.springframework.stereotype.Service;

@Service
public class HotelItemServiceImpl extends ServiceImpl<HotelItemMapper, HotelItem> implements HotelItemService {
}
