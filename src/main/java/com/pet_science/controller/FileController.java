package com.pet_science.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@RestController
@RequestMapping("/statics/images")
public class FileController {
    @GetMapping(value = "/{filePath}",produces = "image/jpeg")
    public byte[] getUserAvatar(@PathVariable("filePath") String filePath){
        byte[] fileBytes = null;
        File file = new File(filePath);
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
