package com.devflow.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Vue SPA 前端路由——非API请求都返回 index.html
 */
@Controller
public class IndexController {
    @RequestMapping(value = {"/", "/login", "/projects/**", "/issues/**"})
    public String index() {
        return "forward:/index.html";
    }
}
