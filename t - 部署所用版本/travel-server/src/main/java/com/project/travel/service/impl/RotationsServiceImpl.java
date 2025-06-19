package com.project.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.travel.domain.Carousel;
import com.project.travel.mapper.RotationsMapper;
import com.project.travel.service.CarouselService;
import org.springframework.stereotype.Service;

@Service
public class RotationsServiceImpl extends ServiceImpl<RotationsMapper, Carousel> implements CarouselService {
}
