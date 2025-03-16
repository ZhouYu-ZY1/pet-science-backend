package com.pet_science.controller;

import com.pet_science.service.VideoService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
class VideoControllerTest {

    @Autowired
    VideoService videoService;



}