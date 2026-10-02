package com.sky.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data 
@AllArgsConstructor 
@Slf4j 
public class LocalOssUtil {
    private String uplocalDir;
    private String urlPath;

    public String upload(byte[] bytes,String objectName) throws IOException{
        Path dir = Paths.get(uplocalDir).toAbsolutePath().normalize();
        Files.createDirectories(dir);

        Path target = dir.resolve(objectName).normalize();
        Files.write(target, bytes);

        String fileUrl = urlPath.endsWith("/")? urlPath + objectName: urlPath + "/" + objectName;
        return fileUrl;
    }
}
