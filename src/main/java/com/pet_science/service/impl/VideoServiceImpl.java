package com.pet_science.service.impl;

import com.pet_science.pojo.Like;
import com.pet_science.pojo.Video;
import com.pet_science.service.VideoService;
import com.pet_science.mapper.VideoMapper;
import com.pet_science.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideoMapper videoMapper;


}
