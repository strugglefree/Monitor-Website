package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Client;
import com.example.entity.dto.ClientDetail;
import com.example.entity.dto.ClientSsh;
import com.example.entity.vo.request.*;
import com.example.entity.vo.response.*;
import com.example.mapper.ClientDetailMapper;
import com.example.mapper.ClientMapper;
import com.example.mapper.ClientSshMapper;
import com.example.service.ClientService;
import com.example.utils.InfluxDBUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ll
 * @description: 客户端功能实现
 * @date 2024/7/27 下午3:10
 */
@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements ClientService {

    @Resource
    private ClientDetailMapper mapper;
    @Resource
    InfluxDBUtils utils;
    @Resource
    ClientSshMapper sshMapper;

    private String registerToken = this.generateToken();

    private final Map<Integer, Client> clientIdCache = new ConcurrentHashMap<>(); //根据ID找客户端的缓存
    private final Map<String, Client> clientTokenCache = new ConcurrentHashMap<>(); //根据Token找客户端的缓存
    @Autowired
    private ClientDetailMapper clientDetailMapper;

    /**
     * @description: 初始化缓存，把数据库里的加进缓存
     * @param: []
     * @return: void
     * @author Ll
     * @date: 2024/7/27 下午6:16
     */
    @PostConstruct
    public void initClientCache() {
        clientIdCache.clear();
        clientTokenCache.clear();
        this.list().forEach(this::addClientCache);
    }

    /**
     * @description: 返回一个注册的Token令牌
     * @param: []
     * @return: java.lang.String
     * @author Ll
     * @date: 2024/7/27 下午3:28
     */
    @Override
    public String registerToken() {
        return registerToken;
    }

    /**
     * @description: 通过ID找到用户端
     * @param: [id]
     * @return: com.example.entity.dto.Client
     * @author Ll
     * @date: 2024/7/27 下午6:21
     */
    @Override
    public Client findClientById(int id) {
        return clientIdCache.get(id);
    }

    /**
     * @description: 通过Token找到用户端
     * @param: [token]
     * @return: com.example.entity.dto.Client
     * @author Ll
     * @date: 2024/7/27 下午6:22
     */
    @Override
    public Client findClientByToken(String token) {
        return clientTokenCache.get(token);
    }

    /**
     * @description: 验证Token并且注册用户端
     * @param: [token]
     * @return: boolean
     * @author Ll
     * @date: 2024/7/27 下午3:33
     */
    @Override
    public boolean verifyAndRegister(String token) {
        if(token.equals(this.registerToken)) {
            int id = this.generateClientID();
            Client client = new Client(id,"未命名主机", token, "cn", "未命名节点", new Date());
            if(this.save(client)) {
                registerToken = this.generateToken();
                this.addClientCache(client);
                return true;
            }
        }
        return false;
    }

    /**
     * @description: 往数据库里面填入客户端信息
     * @param: [clientDetail]
     * @return: void
     * @author Ll
     * @date: 2024/7/30 下午4:09
     */
    @Override
    public void updateClientDetail(ClientDetailVO vo, Client client) {
        ClientDetail detail = new ClientDetail();
        BeanUtils.copyProperties(vo,detail);
        detail.setId(client.getId());
        if(Objects.nonNull(mapper.selectById(client.getId()))) {
            mapper.updateById(detail);
        }else
            mapper.insert(detail);
    }


    private final Map<Integer , RuntimeDetailVO> currentRuntime = new ConcurrentHashMap<>();
    /**
     * @description: 更新运行时的信息
     * @param: [vo, client]
     * @return: void
     * @author Ll
     * @date: 2024/7/31 下午1:25
     */
    @Override
    public void updateRuntimeDetail(RuntimeDetailVO vo, Client client) {
        currentRuntime.put(client.getId(), vo);
        utils.writeRuntimeData(client.getId(),vo);
    }

    /**
     * @description: 获取客户端服务器信息
     * @param: []
     * @return: java.util.List<com.example.entity.vo.response.ClientPreviewVO>
     * @author Ll
     * @date: 2024/8/2 下午4:06
     */
    @Override
    public List<ClientPreviewVO> listClients() {
        return clientIdCache.values().stream().map(client -> {
            ClientPreviewVO vo = client.asViewObject(ClientPreviewVO.class);
            BeanUtils.copyProperties(clientDetailMapper.selectById(client.getId()),vo);
            RuntimeDetailVO RDvo = currentRuntime.get(client.getId());
            if(this.isOnline(RDvo)) {
                BeanUtils.copyProperties(RDvo,vo);
                vo.setOnline(true);
            }
            return vo;
        }).toList();
    }

    /**
     * @description: 获取所有客户端基本信息
     * @param: []
     * @return: java.util.List<com.example.entity.vo.response.ClientSimpleVO>
     * @author Ll
     * @date: 2024/8/13 下午3:37
     */
    @Override
    public List<ClientSimpleVO> listClientSimples() {
        return clientIdCache.values().stream().map(client -> {
            ClientSimpleVO vo = client.asViewObject(ClientSimpleVO.class);
            BeanUtils.copyProperties(clientDetailMapper.selectById(client.getId()),vo);
            return vo;
        }).toList();
    }

    /**
     * @description: 重命名客户端
     * @param: [vo]
     * @return: void
     * @author Ll
     * @date: 2024/8/3 下午12:46
     */
    @Override
    public void renameClient(RenameClientVO vo) {
        this.update(Wrappers.<Client>update().eq("id", vo.getId()).set("name", vo.getName()));
        this.initClientCache();
    }

    /**
     * @description: 获取客户端详细信息
     * @param: [clientId]
     * @return: com.example.entity.vo.response.ClientDetailsVO
     * @author Ll
     * @date: 2024/8/9 下午5:31
     */
    @Override
    public ClientDetailsVO getClientDetails(int clientId) {
        ClientDetailsVO vo = this.clientIdCache.get(clientId).asViewObject(ClientDetailsVO.class);
        BeanUtils.copyProperties(clientDetailMapper.selectById(clientId),vo);
        vo.setOnline(this.isOnline(currentRuntime.get(clientId)));
        return vo;
    }

    /**
     * @description: 重命名节点
     * @param: [vo]
     * @return: void
     * @author Ll
     * @date: 2024/8/9 下午5:31
     */
    @Override
    public void renameNode(RenameNodeVO vo) {
        this.update(Wrappers.<Client>update().eq("id", vo.getId())
                .set("node", vo.getNode()).set("location", vo.getLocation()));
        this.initClientCache();
    }

    /**
     * @description: 获取一个小时内的运行数据
     * @param: [clientId]
     * @return: com.example.entity.vo.response.RuntimeDetailsVO
     * @author Ll
     * @date: 2024/8/11 上午7:55
     */
    @Override
    public RuntimeDetailsVO getRuntimeDetailsHistory(int clientId) {
        RuntimeDetailsVO vo = utils.readRuntimeData(clientId);
        ClientDetail detail = clientDetailMapper.selectById(clientId);
        BeanUtils.copyProperties(detail,vo);
        return vo;
    }

    /**
     * @description: 获取现在的运行时数据
     * @param: [clientId]
     * @return: com.example.entity.vo.request.RuntimeDetailVO
     * @author Ll
     * @date: 2024/8/11 上午7:55
     */
    @Override
    public RuntimeDetailVO getRuntimeDetailNow(int clientId) {
        return currentRuntime.get(clientId);
    }

    /**
     * @description: 删除客户端
     * @param: [clientId]
     * @return: void
     * @author Ll
     * @date: 2024/8/11 下午1:15
     */
    @Override
    public void deleteClient(int clientId) {
       this.removeById(clientId);
       mapper.deleteById(clientId);
       this.initClientCache();
       currentRuntime.remove(clientId);
    }

    /**
     * @description: 保存ssh连接信息
     * @param: [vo]
     * @return: void
     * @author Ll
     * @date: 2024/8/16 下午3:35
     */
    @Override
    public void saveClientSshConnection(SshConnectionVO vo) {
        Client client = clientIdCache.get(vo.getId());
        if(!Objects.nonNull(client)) { return; }
        ClientSsh ssh = new ClientSsh();
        BeanUtils.copyProperties(vo,ssh);
        if(Objects.nonNull(sshMapper.selectById(vo.getId()))) {
            sshMapper.updateById(ssh);
        }else
            sshMapper.insert(ssh);
    }

    /**
     * @description: 获取ssh连接设置
     * @param: [clientId]
     * @return: com.example.entity.vo.response.SshSettingVO
     * @author Ll
     * @date: 2024/8/16 下午3:36
     */
    @Override
    public SshSettingVO getSshSetting(int clientId) {
        ClientDetail detail = clientDetailMapper.selectById(clientId);
        ClientSsh ssh = sshMapper.selectById(clientId);
        SshSettingVO vo;
        if(Objects.nonNull(ssh)) {
            vo = ssh.asViewObject(SshSettingVO.class);
        }else
            vo = new SshSettingVO();
        vo.setIp(detail.getIp());
        return vo;
    }

    /**
     * @description: 客户端是否在线
     * @param: [vo]
     * @return: boolean
     * @author Ll
     * @date: 2024/8/3 下午3:26
     */
    private boolean isOnline(RuntimeDetailVO vo) {
        return Objects.nonNull(vo) && System.currentTimeMillis() - vo.getTimestamp() < 60 * 1000;
    }

    /**
     * @description: 将客户端添加进缓存
     * @param: [client]
     * @return: void
     * @author Ll
     * @date: 2024/7/27 下午6:14
     */
    private void addClientCache(Client client) {
        clientIdCache.put(client.getId(), client);
        clientTokenCache.put(client.getToken(), client);
    }

    /**
     * @description: 随机生成一个注册ID
     * @param: []
     * @return: int
     * @author Ll
     * @date: 2024/7/27 下午3:30
     */
    private int generateClientID(){
        return new Random().nextInt(90000000) + 10000000;
    }

    /**
     * @description: 随机生成一个Token
     * @param: []
     * @return: java.lang.String
     * @author Ll
     * @date: 2024/7/27 下午3:25
     */
    private String generateToken() {
        String CHARACTERS = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(24);
        for (int i = 0; i < 24; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
