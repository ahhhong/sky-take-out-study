package com.sky.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;


@Component 
@ConfigurationProperties (prefix = "sky.local")
@Data 
public class LocalOssProperties {
    private String uplocalDir;
    private String urlPath;
}
