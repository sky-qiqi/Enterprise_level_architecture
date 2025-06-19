package com.project.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.travel.domain.Favorite;
import com.project.travel.mapper.FavorMapper;
import com.project.travel.service.FavoriteService;
import org.springframework.stereotype.Service;

@Service
public class FavorServiceImpl extends ServiceImpl<FavorMapper, Favorite> implements FavoriteService {
}
