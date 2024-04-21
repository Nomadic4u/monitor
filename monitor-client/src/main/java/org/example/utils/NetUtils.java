package org.example.utils;


import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.ConnectionConfig;
import org.example.entity.Response;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Component
public class NetUtils {

    @Lazy // 用的时候再去取, 避免循环引用
    @Resource
    ConnectionConfig config;

    // jdk11提供的网络工具
    private final HttpClient client = HttpClient.newHttpClient();

    public boolean registerToServer(String address, String token) {
        log.info("正在向服务端注册, 请稍后");
        Response response = this.doGet("/register", address, token);
        if (response.sucess()) {
            log.info("客户端注册已完成! ");
        } else {
            log.error("客户端注册失败: {}", response.message());
        }
        return response.sucess();
    }

    private Response doGet(String url) {
        return this.doGet(url, config.getAdress(), config.getToken());
    }


    // 给服务端发送请求
    private Response doGet(String url, String address, String token) {
        try {
            HttpRequest request = HttpRequest.newBuilder().GET()
                    .uri(new URI(address + "/monitor" + url))
                    .header("Authorization", token)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return JSONObject.parseObject(response.body()).to(Response.class);
        } catch (Exception e) {
            log.error("在发起服务端请求时出现错误", e);
            return Response.errorResponse(e);
        }
    }
}
