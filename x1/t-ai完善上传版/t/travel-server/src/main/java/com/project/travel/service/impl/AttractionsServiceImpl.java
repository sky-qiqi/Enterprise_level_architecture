package com.project.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.travel.domain.Attractions;
import com.project.travel.mapper.ChildTransactionality;
import com.project.travel.service.AttractionsService;
import org.springframework.stereotype.Service;

@Service
public class AttractionsServiceImpl extends ServiceImpl<ChildTransactionality, Attractions> implements AttractionsService {
}
