package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.ChangePasswordVO;
import com.example.service.AccountService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author Ll
 * @description: 用户相关操作接口
 * @date 2024/8/13 上午11:16
 */
@RestController
@RequestMapping("/api/user")
public class AccountController {

    @Resource
    AccountService service;

    @PostMapping("/change-password")
    public RestBean<Void> changePassword(@RequestBody @Valid ChangePasswordVO vo,
                                         @RequestAttribute(Const.ATTR_USER_ID) int uid) {
        if(service.changePassword(vo, uid))
            return RestBean.success();
        else return RestBean.failure(401, "原密码错误，请重试");
    }
}
