package com.example.utils;

import com.alibaba.fastjson2.JSONObject;
import com.example.entity.BaseDetail;
import com.example.entity.ConnectionConfig;
import com.example.entity.Response;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author Ll
 * @description: 发起请求的网络工具
 * @date 2024/7/28 下午2:48
 */
@Slf4j
@Component
public class NetUtil {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Lazy
    @Resource
    ConnectionConfig config;

    /**
     * @description: 向服务器请求注册
     * @param: [address, token]
     * @return: boolean
     * @author Ll
     * @date: 2024/7/28 下午3:20
     */
    public boolean registerToServer(String address, String token){
        log.info("正在向服务器注册，请稍后...");
        Response response = this.doGet("/register", address, token);
        if(response.success()){
            log.info("客户端注册已完成");
        }else {
            log.error("客户端注册失败：{}", response.message());
        }
        return response.success();
    }

    /**
     * @description: 将客户端自身相关参数发给服务端
     * @param: [baseDetail]
     * @return: void
     * @author Ll
     * @date: 2024/7/30 下午4:39
     */
    public void updateBaseDetail(BaseDetail baseDetail){
        Response response = this.doPost("/detail", baseDetail);
        if(response.success()){
            log.info("系统基本信息已更新完成");
        }else
            log.error("系统基本信息更新失败：{}", response.message());
    }

    /**
     * @description: 发起POST请求
     * @param: [url, data]
     * @return: com.example.entity.Response
     * @author Ll
     * @date: 2024/7/30 下午4:35
     */
    private Response doPost(String url, Object data) {
        try {
            String rawData = JSONObject.from(data).toJSONString();
            HttpRequest httpRequest = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(rawData))
                    .uri(new URI(config.getAddress() + "/monitor" + url))
                    .header("Authorization", config.getToken())
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return JSONObject.parseObject(response.body(),Response.class);
        }catch (Exception e){
            log.error("在发起服务端请求时出现问题",e);
            return Response.errorResponse(e);
        }
    }

    /**
     * @description: 发起GET请求
     * @param: [url, address, token]
     * @return: com.example.entity.Response
     * @author Ll
     * @date: 2024/7/28 下午3:20
     */
    private Response doGet(String url, String address, String token) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(address + "/monitor" + url))
                    .header("Authorization", token)
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return JSONObject.parseObject(response.body(),Response.class);
        }catch (Exception e){
            log.error("在发起服务端请求时出现问题",e);
            return Response.errorResponse(e);
        }
    }
    /**
     * @description: 重载doGet方法
     * @param: [url]
     * @return: com.example.entity.Response
     * @author Ll
     * @date: 2024/7/28 下午3:38
     */
    private Response doGet(String url) {
        return this.doGet(url, config.getAddress(), config.getToken());
    }
}
