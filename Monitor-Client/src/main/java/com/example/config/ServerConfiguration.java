package com.example.config;

import com.alibaba.fastjson2.JSONObject;
import com.example.entity.ConnectionConfig;
import com.example.utils.MonitorUtils;
import com.example.utils.NetUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author Ll
 * @description: 与服务器相连的配置类
 * @date 2024/7/28 下午2:24
 */
@Slf4j
@Configuration
public class ServerConfiguration {

    @Resource
    private NetUtil net;

    @Resource
    private MonitorUtils utils;

    /**
     * @description: 连接服务器
     * @param: []
     * @return: com.example.entity.ConnectionConfig
     * @author Ll
     * @date: 2024/7/28 下午3:24
     */
    @Bean
    ConnectionConfig connectionConfig(){
        log.info("正在加载服务器链接配置......");
        ConnectionConfig connectionConfig = this.readConfigurationFromFile();
        if(connectionConfig == null){
            connectionConfig = this.registerToServer();
        }
        System.out.println(utils.monitorBaseDetail());
        return connectionConfig;
    }

    /**
     * @description: 输入对应的值，并向服务端注册
     * @param: []
     * @return: com.example.entity.ConnectionConfig
     * @author Ll
     * @date: 2024/7/28 下午3:22
     */
    private ConnectionConfig registerToServer(){
        Scanner scanner = new Scanner(System.in);
        String address, token;
        do{
            log.info("请输入需要注册的服务端访问地址,地址类似于:'http://192.168.0.10':");
            address = scanner.nextLine();
            log.info("请输入服务端生成的用于注册客户端的Token密钥:");
            token = scanner.nextLine();
        }while (!net.registerToServer(address,token));
        ConnectionConfig config = new ConnectionConfig(address,token);
        this.saveConfigurationToFile(config);
        return config;
    }

    /**
     * @description: 将连接服务端信息保存到本地
     * @param: [config]
     * @return: void
     * @author Ll
     * @date: 2024/7/28 下午3:32
     */
    private void saveConfigurationToFile(ConnectionConfig config){
        File dir = new File("config");
        if(!dir.exists() && dir.mkdirs()){
            log.info("创建用于保存服务端连接信息的目录已完成");
        }
        File file = new File("config/server.json");
        try(FileWriter writer = new FileWriter(file)){
          writer.write(JSONObject.toJSONString(config));
        }catch (IOException e){
           log.error("保存配置文件时出现问题", e);
        }
        log.info("服务端连接信息已保存成功！");
    }

    /**
     * @description: 读取配置文件
     * @param: []
     * @return: com.example.entity.ConnectionConfig
     * @author Ll
     * @date: 2024/7/28 下午2:40
     */
    private ConnectionConfig readConfigurationFromFile(){
        File file = new File("config/server.json");
        if(file.exists()){
            try(FileInputStream stream = new FileInputStream(file)){
                String raw = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                return JSONObject.parseObject(raw, ConnectionConfig.class);
            }catch (IOException e){
                log.error("读取配置文件时出错", e);
            }
        }
        return null;
    }
}
