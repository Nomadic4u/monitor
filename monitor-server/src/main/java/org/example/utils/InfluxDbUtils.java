package org.example.utils;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import jakarta.annotation.PostConstruct;
import org.example.entity.dto.RuntimeData;
import org.example.entity.vo.request.RuntimeDetailVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class InfluxDbUtils {

    @Value("${spring.influx.url}")
    String url;

    @Value("${spring.influx.user}")
    String user;

    @Value("${spring.influx.password}")
    String password;

    private final String BUCKET = "monitor";

    private final String ORG = "block";


    // 首先创建一个客户端
    private InfluxDBClient client;

    @PostConstruct
    public void init() {
        client = InfluxDBClientFactory.create(url, user, password.toCharArray());
    }

    // Influxdb写数据
    public void writeRuntimeData(int clientId, RuntimeDetailVo vo) {
        RuntimeData data = new RuntimeData();
        BeanUtils.copyProperties(vo, data);
//        System.out.println("WWW + " + data);
        data.setTimestamp(new Date(vo.getTimestamp()).toInstant());
        data.setClientId(clientId);
        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writeMeasurement(BUCKET, ORG, WritePrecision.NS, data);

    }
}
