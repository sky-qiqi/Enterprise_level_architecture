package com.project.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.travel.domain.ForumPost;
import com.project.travel.mapper.ForumMapper;
import com.project.travel.service.ForumService;
import org.springframework.stereotype.Service;

@Service
public class ForumServiceImpl extends ServiceImpl<ForumMapper, ForumPost> implements ForumService {
}
