package com.pet_science.controller;


import com.pet_science.pojo.Like;
import com.pet_science.pojo.Video;
import com.pet_science.service.VideoService;
import com.alibaba.fastjson.JSONObject;
import com.pet_science.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

}
