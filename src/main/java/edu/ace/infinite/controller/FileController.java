package edu.ace.infinite.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static edu.ace.infinite.controller.UserController.USER_AVATAR_PATH;

@RestController
@RequestMapping("/statics/images")
public class FileController {
    @GetMapping(value = "/userAvatar/{fileName}",produces = "image/jpeg")
    public byte[] getUserAvatar(@PathVariable("fileName") String fileName){
        System.err.println(fileName);
        byte[] fileBytes = null;
        File file = new File(USER_AVATAR_PATH + fileName);
        if(file.exists()){
            try {
                fileBytes = readBytesFromInputStream(Files.newInputStream(file.toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileBytes;
    }
    public byte[] readBytesFromInputStream(InputStream input) {
        try {
            BufferedInputStream bufferedInput = new BufferedInputStream(input);
            byte[] result = new byte[bufferedInput.available()];
            bufferedInput.read(result, 0, result.length);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
