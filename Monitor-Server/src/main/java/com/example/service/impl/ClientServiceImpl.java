package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Client;
import com.example.entity.dto.ClientDetail;
import com.example.entity.vo.request.ClientDetailVO;
import com.example.entity.vo.request.RenameClientVO;
import com.example.entity.vo.request.RuntimeDetailVO;
import com.example.entity.vo.response.ClientDetailsVO;
import com.example.entity.vo.response.ClientPreviewVO;
import com.example.mapper.ClientDetailMapper;
import com.example.mapper.ClientMapper;
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

    @Override
    public ClientDetailsVO getClientDetails(int clientId) {
        ClientDetailsVO vo = this.clientIdCache.get(clientId).asViewObject(ClientDetailsVO.class);
        BeanUtils.copyProperties(clientDetailMapper.selectById(clientId),vo);
        vo.setOnline(this.isOnline(currentRuntime.get(clientId)));
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
        String CHARACTERS = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(24);
        for (int i = 0; i < 24; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        System.out.println(sb);
        return sb.toString();
    }
}
