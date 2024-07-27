package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Client;
import com.example.mapper.ClientMapper;
import com.example.service.ClientService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ll
 * @description: 客户端功能实现
 * @date 2024/7/27 下午3:10
 */
@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements ClientService {

    private String registerToken = this.generateToken();

    private final Map<Integer, Client> clientIdCache = new ConcurrentHashMap<>(); //根据ID找客户端的缓存
    private final Map<String, Client> clientTokenCache = new ConcurrentHashMap<>(); //根据Token找客户端的缓存

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
            Client client = new Client(id,"未命名主机", token, new Date());
            if(this.save(client)) {
                registerToken = this.generateToken();
                this.addClientCache(client);
                return true;
            }
        }
        return false;
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
        return sb.toString();
    }
}
