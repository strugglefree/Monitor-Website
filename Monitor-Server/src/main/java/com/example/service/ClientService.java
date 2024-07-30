package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Client;
import com.example.entity.vo.request.ClientDetailVO;

/**
 * @author Ll
 * @description: 客户端功能
 * @date 2024/7/27 下午3:08
 */
public interface ClientService extends IService<Client> {
    String registerToken();
    Client findClientById(int id);
    Client findClientByToken(String token);
    boolean verifyAndRegister(String token);
    void updateClientDetail(ClientDetailVO vo, Client client);
}
