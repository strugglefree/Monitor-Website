package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.RenameClientVO;
import com.example.entity.vo.response.ClientDetailsVO;
import com.example.entity.vo.response.ClientPreviewVO;
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

    @PostMapping("/rename")
    public RestBean<Void> renameClient(@RequestBody @Valid RenameClientVO vo){
        service.renameClient(vo);
        return RestBean.success();
    }

    @GetMapping("/details")
    public RestBean<ClientDetailsVO> detailsClient(int clientId){
        return RestBean.success(service.getClientDetails(clientId));
    }
}
