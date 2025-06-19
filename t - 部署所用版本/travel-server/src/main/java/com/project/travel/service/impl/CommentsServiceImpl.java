package com.project.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.travel.domain.Comments;
import com.project.travel.mapper.Commentsmapper;
import com.project.travel.service.CommentsService;
import org.springframework.stereotype.Service;

@Service
public class CommentsServiceImpl extends ServiceImpl<Commentsmapper, Comments> implements CommentsService {
}
