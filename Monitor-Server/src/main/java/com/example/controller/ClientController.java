package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.ClientService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ll
 * @description: 客户端接口
 * @date 2024/7/27 下午3:01
 */
@RestController
@RequestMapping("/monitor")
public class ClientController {

    @Resource
    private ClientService clientService;

    @GetMapping("/register")
    public RestBean<Void> register(@RequestHeader("Authorization") String token) {
        return clientService.verifyAndRegister(token) ?
                RestBean.success() : RestBean.failure(401,"客户端注册失败，请检查Token是否正确");
    }
}
