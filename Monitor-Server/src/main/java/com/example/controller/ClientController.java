package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.Client;
import com.example.entity.vo.request.ClientDetailVO;
import com.example.entity.vo.request.RuntimeDetailVO;
import com.example.service.ClientService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/detail")
    public RestBean<Void> updateClientDetail(@RequestAttribute(Const.ATTR_Client) Client client,
                                             @RequestBody @Valid ClientDetailVO vo){
        clientService.updateClientDetail(vo, client);
        return RestBean.success();
    }

    @PostMapping("/runtime")
    public RestBean<Void> updateRuntimeDetail(@RequestAttribute(Const.ATTR_Client) Client client,
                                              @RequestBody @Valid RuntimeDetailVO vo){
        clientService.updateRuntimeDetail(vo, client);
        return RestBean.success();
    }
}
