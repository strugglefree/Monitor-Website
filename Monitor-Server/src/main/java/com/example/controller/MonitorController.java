package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.RenameClientVO;
import com.example.entity.vo.request.RenameNodeVO;
import com.example.entity.vo.request.RuntimeDetailVO;
import com.example.entity.vo.response.ClientDetailsVO;
import com.example.entity.vo.response.ClientPreviewVO;
import com.example.entity.vo.response.ClientSimpleVO;
import com.example.entity.vo.response.RuntimeDetailsVO;
import com.example.service.ClientService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Ll
 * @description: 与前端对接
 * @date 2024/8/2 下午3:22
 */
@RestController
@RequestMapping("/api/monitor")
public class MonitorController {

    @Resource
    private ClientService service;

    @GetMapping("/list")
    public RestBean<List<ClientPreviewVO>> listAllClient(){
        return RestBean.success(service.listClients());
    }

    @GetMapping("/simple-list")
    public RestBean<List<ClientSimpleVO>> listSimpleClient(){
        return RestBean.success(service.listClientSimples());
    }

    @PostMapping("/rename")
    public RestBean<Void> renameClient(@RequestBody @Valid RenameClientVO vo){
        service.renameClient(vo);
        return RestBean.success();
    }

    @PostMapping("/node")
    public RestBean<Void> renameNode(@RequestBody @Valid RenameNodeVO vo){
        service.renameNode(vo);
        return RestBean.success();
    }

    @GetMapping("/details")
    public RestBean<ClientDetailsVO> detailsClient(int clientId){
        return RestBean.success(service.getClientDetails(clientId));
    }

    @GetMapping("/runtime-history")
    public RestBean<RuntimeDetailsVO> runtimeHistoryClient(int clientId){
        return RestBean.success(service.getRuntimeDetailsHistory(clientId));
    }

    @GetMapping("/runtime-now")
    public RestBean<RuntimeDetailVO> runtimeNowClient(int clientId){
        return RestBean.success(service.getRuntimeDetailNow(clientId));
    }

    @GetMapping("/register")
    public RestBean<String> registerToken(){
        return RestBean.success(service.registerToken());
    }

    @GetMapping("/delete")
    public RestBean<Void> deleteClient(int clientId){
        service.deleteClient(clientId);
        return RestBean.success();
    }
}
