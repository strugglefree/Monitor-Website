package com.example.utils;

import com.alibaba.fastjson2.JSONObject;
import com.example.entity.dto.RuntimeData;
import com.example.entity.vo.request.RuntimeDetailVO;
import com.example.entity.vo.response.RuntimeDetailsVO;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author Ll
 * @description: influxDB工具
 * @date 2024/7/31 下午2:43
 */
@Component
public class InfluxDBUtils {

    @Value("${spring.influx.url}")
    private String url;
    @Value("${spring.influx.user}")
    private String user;
    @Value("${spring.influx.password}")
    private String password;

    private final String BUCKET = "monitor";
    private final String ORG = "influxDB";

    private InfluxDBClient client;

    @PostConstruct
    public void init() {
        client = InfluxDBClientFactory.create(url, user, password.toCharArray());
    }

    public void writeRuntimeData(int clientId, RuntimeDetailVO vo){
        RuntimeData data = new RuntimeData();
        BeanUtils.copyProperties(vo, data);
        data.setTimestamp(new Date().toInstant());
        data.setClientId(clientId);
        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writeMeasurement(BUCKET, ORG, WritePrecision.NS, data);
    }

    public RuntimeDetailsVO readRuntimeData(int clientId){
        RuntimeDetailsVO vo = new RuntimeDetailsVO();
        String query = """
                from(bucket: "%s")
                |> range(start: %s)
                |> filter(fn: (r) => r["_measurement"] == "runtime")
                |> filter(fn: (r) => r["clientId"] == "%s")
                """;
        String format = String.format(query, BUCKET, "-1h", clientId);
        List<FluxTable> tables = client.getQueryApi().query(format, ORG);
        int size = tables.size();
        if(size == 0) return vo;
        List<FluxRecord> records = tables.get(0).getRecords();
        for(int i = 0; i < records.size(); i++){
            JSONObject object = new JSONObject();
            object.put("timestamp", records.get(1).getTime());
            for(int j = 0; j < size; j++){
                FluxRecord record = tables.get(j).getRecords().get(i);
                object.put(record.getField(), record.getValue());
            }
            vo.getList().add(object);
        }
        return vo;
    }
}
