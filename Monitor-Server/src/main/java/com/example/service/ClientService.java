package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Client;
import com.example.entity.vo.request.*;
import com.example.entity.vo.response.*;

import java.util.List;

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
    void updateRuntimeDetail(RuntimeDetailVO vo, Client client);
    List<ClientPreviewVO> listClients();
    List<ClientSimpleVO> listClientSimples();
    void renameClient(RenameClientVO vo);
    ClientDetailsVO getClientDetails(int clientId);
    void renameNode(RenameNodeVO vo);
    RuntimeDetailsVO getRuntimeDetailsHistory(int clientId);
    RuntimeDetailVO getRuntimeDetailNow(int clientId);
    void deleteClient(int clientId);
    void saveClientSshConnection(SshConnectionVO vo);
    SshSettingVO getSshSetting(int clientId);
}
