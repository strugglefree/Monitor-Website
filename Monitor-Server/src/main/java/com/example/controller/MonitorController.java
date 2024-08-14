package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.vo.request.RenameClientVO;
import com.example.entity.vo.request.RenameNodeVO;
import com.example.entity.vo.request.RuntimeDetailVO;
import com.example.entity.vo.response.ClientDetailsVO;
import com.example.entity.vo.response.ClientPreviewVO;
import com.example.entity.vo.response.ClientSimpleVO;
import com.example.entity.vo.response.RuntimeDetailsVO;
import com.example.service.AccountService;
import com.example.service.ClientService;
import com.example.utils.Const;
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

    @Resource
    private AccountService accountService;

    @GetMapping("/list")
    public RestBean<List<ClientPreviewVO>> listAllClient(@RequestAttribute(Const.ATTR_USER_ID) int userId,
                                                         @RequestAttribute(Const.ATTR_USER_ROLE) String role){
        List<ClientPreviewVO> clients = service.listClients();
        if(this.isAdminAccount(role)) return RestBean.success(clients);
        else {
            List<Integer> list = this.accountAccessClients(userId);
            return RestBean.success(clients.stream()
                    .filter(vo -> list.contains(vo.getId()))
                    .toList());
        }
    }

    @GetMapping("/simple-list")
    public RestBean<List<ClientSimpleVO>> listSimpleClient(@RequestAttribute(Const.ATTR_USER_ROLE) String role){
        if(this.isAdminAccount(role)){
            return RestBean.success(service.listClientSimples());
        }
        else return RestBean.noPermission();
    }

    @PostMapping("/rename")
    public RestBean<Void> renameClient(@RequestBody @Valid RenameClientVO vo,
                                       @RequestAttribute(Const.ATTR_USER_ID) int userId,
                                       @RequestAttribute(Const.ATTR_USER_ROLE) String role){
        if(this.permissionCheck(userId, role, vo.getId())){
            service.renameClient(vo);
            return RestBean.success();
        }else return RestBean.noPermission();
    }

    @PostMapping("/node")
    public RestBean<Void> renameNode(@RequestBody @Valid RenameNodeVO vo,
                                     @RequestAttribute(Const.ATTR_USER_ID) int userId,
                                     @RequestAttribute(Const.ATTR_USER_ROLE) String role){
        if(this.permissionCheck(userId, role, vo.getId())){
            service.renameNode(vo);
            return RestBean.success();
        }else return RestBean.noPermission();
    }

    @GetMapping("/details")
    public RestBean<ClientDetailsVO> detailsClient(int clientId,
                                                   @RequestAttribute(Const.ATTR_USER_ID) int userId,
                                                   @RequestAttribute(Const.ATTR_USER_ROLE) String role){
        if(this.permissionCheck(userId, role, clientId)){
            return RestBean.success(service.getClientDetails(clientId));
        }else return RestBean.noPermission();
    }

    @GetMapping("/runtime-history")
    public RestBean<RuntimeDetailsVO> runtimeHistoryClient(int clientId,
                                                           @RequestAttribute(Const.ATTR_USER_ID) int userId,
                                                           @RequestAttribute(Const.ATTR_USER_ROLE) String role){
        if(this.permissionCheck(userId, role, clientId)){
            return RestBean.success(service.getRuntimeDetailsHistory(clientId));
        }else return RestBean.noPermission();
    }

    @GetMapping("/runtime-now")
    public RestBean<RuntimeDetailVO> runtimeNowClient(int clientId,
                                                      @RequestAttribute(Const.ATTR_USER_ID) int userId,
                                                      @RequestAttribute(Const.ATTR_USER_ROLE) String role){
        if(this.permissionCheck(userId, role, clientId)){
            return RestBean.success(service.getRuntimeDetailNow(clientId));
        }else return RestBean.noPermission();
    }

    @GetMapping("/register")
    public RestBean<String> registerToken(@RequestAttribute(Const.ATTR_USER_ROLE) String role){
        if(this.isAdminAccount(role)){
            return RestBean.success(service.registerToken());
        }else return RestBean.noPermission();
    }

    @GetMapping("/delete")
    public RestBean<Void> deleteClient(int clientId,
                                       @RequestAttribute(Const.ATTR_USER_ROLE) String role){
        if(this.isAdminAccount(role)){
            service.deleteClient(clientId);
            return RestBean.success();
        }else return RestBean.noPermission();
    }

    /**
     * @description: 获取管理员账号下的所有客户端ID
     * @param: [uid]
     * @return: java.util.List<java.lang.Integer>
     * @author Ll
     * @date: 2024/8/14 下午1:59
     */
    private List<Integer> accountAccessClients(int uid){
        Account account = accountService.getById(uid);
        return account.getClientList();
    }

    /**
     * @description: 判断是否是管理员权限
     * @param: [role]
     * @return: boolean
     * @author Ll
     * @date: 2024/8/14 下午2:02
     */
    private boolean isAdminAccount(String role){
        role = role.substring(5);
        return role.equals(Const.ROLE_DEFAULT);
    }

    /**
     * @description: 权限检查
     * @param: [uid, role, clientId]
     * @return: boolean
     * @author Ll
     * @date: 2024/8/14 下午2:38
     */
    private boolean permissionCheck(int uid, String role, int clientId){
        if(this.isAdminAccount(role)) return true;
        else
            return this.accountAccessClients(uid).contains(clientId);
    }
}
