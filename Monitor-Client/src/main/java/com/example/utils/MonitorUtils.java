package com.example.utils;

import com.example.entity.BaseDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;

import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Ll
 * @description: 检测出服务器（主机）相关配置
 * @date 2024/7/30 下午2:48
 */
@Slf4j
@Component
public class MonitorUtils {
    private final SystemInfo info = new SystemInfo();
    private final Properties properties = System.getProperties();

    /**
     * @description: 获取服务器（主机）基本信息
     * @param: []
     * @return: com.example.entity.BaseDetail
     * @author Ll
     * @date: 2024/7/30 下午3:13
     */
    public BaseDetail monitorBaseDetail() {
        OperatingSystem operatingSystem = info.getOperatingSystem();
        HardwareAbstractionLayer hardware = info.getHardware();
        double memory = hardware.getMemory().getTotal() / 1024.0 / 1024 / 1024;
        double diskSize = Arrays.stream(File.listRoots()).mapToLong(File::getTotalSpace).sum() / 1024.0 / 1024 / 1024;
        String ip = Objects.requireNonNull(this.findNetworkInterface(hardware)).getIPv4addr()[0];
        return new BaseDetail()
                .setOsArch(properties.getProperty("os.arch"))
                .setOsName(operatingSystem.getFamily())
                .setOsVersion(operatingSystem.getVersionInfo().getVersion())
                .setOsBit(operatingSystem.getBitness())
                .setCpuName(hardware.getProcessor().getProcessorIdentifier().getName())
                .setCpuCore(hardware.getProcessor().getPhysicalProcessorCount())
                .setMemory(memory)
                .setDisk(diskSize)
                .setIp(ip);
    }

    /**
     * @description: 获取网卡的IPV4地址
     * @param: [hardware]
     * @return: oshi.hardware.NetworkIF
     * @author Ll
     * @date: 2024/7/30 下午3:14
     */
    private NetworkIF findNetworkInterface(HardwareAbstractionLayer hardware){
        try {
            for(NetworkIF networkIF : hardware.getNetworkIFs()){
                String[] ipv4 = networkIF.getIPv4addr();
                NetworkInterface networkInterface = networkIF.queryNetworkInterface();
                if(networkInterface.isUp() && !networkInterface.isLoopback()
                && !networkInterface.isVirtual() && !networkInterface.isPointToPoint()
                && (networkInterface.getName().startsWith("eth") || networkInterface.getName().startsWith("en"))
                && ipv4.length > 0){
                    return networkIF;
                }
            }
        }catch (IOException e){
            log.error("读取网卡接口信息出错",e);
        }
        return null;
    }
}
