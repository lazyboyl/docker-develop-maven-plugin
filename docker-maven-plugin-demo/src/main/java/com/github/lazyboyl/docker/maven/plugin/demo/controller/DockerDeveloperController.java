package com.docker.developer.maven.plugin.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author  linzf
 * @since 15:30
 * 类描述：docker插件测试controller类
 */
@RestController
public class DockerDeveloperController {

    @GetMapping("test")
    public String test(){
        return "test";
    }


}
