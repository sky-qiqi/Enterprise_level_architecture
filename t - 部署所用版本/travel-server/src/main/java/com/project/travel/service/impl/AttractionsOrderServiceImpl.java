package com.project.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.travel.domain.AttractionOrder;
import com.project.travel.mapper.AttractionOrdermapper;
import com.project.travel.service.AttractionsOrderService;
import org.springframework.stereotype.Service;

@Service
public class AttractionsOrderServiceImpl extends ServiceImpl<AttractionOrdermapper, AttractionOrder> implements AttractionsOrderService {
}
