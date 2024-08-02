package com.example.utils;

import com.example.entity.BaseDetail;
import com.example.entity.RuntimeDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;

import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Date;
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
     * @description: 获取客户端服务器（主机）基本信息
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
                .setCpuCore(hardware.getProcessor().getLogicalProcessorCount())
                .setMemory(memory)
                .setDisk(diskSize)
                .setIp(ip);
    }

    /**
     * @description: 获取客户端服务器（主机）运行时数据
     * @param: []
     * @return: com.example.entity.RuntimeDetail
     * @author Ll
     * @date: 2024/7/31 下午12:13
     */
    public RuntimeDetail monitorRuntimeDetail() {
        double statisticTime = 0.5;
        try{
           HardwareAbstractionLayer hardware = info.getHardware();
           NetworkIF networkInterface = Objects.requireNonNull(this.findNetworkInterface(hardware));
           CentralProcessor processor = hardware.getProcessor();
           double upload = networkInterface.getBytesSent();
           double download = networkInterface.getPacketsRecv();
           double read = hardware.getDiskStores().stream().mapToLong(HWDiskStore::getReadBytes).sum();
           double write = hardware.getDiskStores().stream().mapToLong(HWDiskStore::getWriteBytes).sum();
           long[] ticks = processor.getSystemCpuLoadTicks();
           Thread.sleep((long)(statisticTime * 1000));
           networkInterface = Objects.requireNonNull(this.findNetworkInterface(hardware));
           upload = (networkInterface.getBytesSent() - upload) / statisticTime;
           download = (networkInterface.getPacketsRecv() - download) / statisticTime;
           read = (hardware.getDiskStores().stream().mapToLong(HWDiskStore::getReadBytes).sum() - read) / statisticTime;
           write = (hardware.getDiskStores().stream().mapToLong(HWDiskStore::getWriteBytes).sum() - write) / statisticTime;
           double memory = (hardware.getMemory().getTotal() - hardware.getMemory().getAvailable()) / 1024.0 / 1024 / 1024;
           double disk = Arrays.stream(File.listRoots()).mapToLong(file ->
                   file.getTotalSpace() - file.getFreeSpace()).sum() / 1024.0 / 1024 / 1024;
            return new RuntimeDetail()
                    .setCpuUsage(this.calculateCpuUsage(processor, ticks))
                    .setMemoryUsage(memory)
                    .setDiskUsage(disk)
                    .setNetworkUpload(upload / 1024)
                    .setNetworkDownload(download / 1024)
                    .setDiskRead(read / 1024/ 1024)
                    .setDiskWrite(write / 1024 / 1024)
                    .setTimestamp(new Date().getTime());

        }catch (Exception e){
            log.error("读取运行时数据出错", e);
        }
        return null;
    }

    /**
     * @description: 计算cpu占用率
     * @param: [processor, prevTicks]
     * @return: double
     * @author Ll
     * @date: 2024/7/31 下午12:13
     */
    private double calculateCpuUsage(CentralProcessor processor, long[] prevTicks) {
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()]
                - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softIrq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()]
                - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()]
                - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long cUser = ticks[CentralProcessor.TickType.USER.getIndex()]
                - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long ioWait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()]
                - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()]
                - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = cUser + nice + cSys + idle + ioWait + irq + softIrq + steal;
        return (cSys + cUser) * 1.0 / totalCpu;
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
                && (networkInterface.getName().startsWith("eth") || networkInterface.getName().startsWith("en") || networkInterface.getName().startsWith("wlan"))
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
