package edu.ace.infinite.controller;

import edu.ace.infinite.pojo.Video;
import edu.ace.infinite.service.VideoService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
class VideoControllerTest {

    @Autowired
    VideoService videoService;



}