package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.BaseDetail;
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

@Slf4j
@Component
public class MonitorUtils {

    private final SystemInfo info = new SystemInfo(); // 获取硬件信息
    private final Properties properties = System.getProperties();
    ;

    public BaseDetail monitorBaseDetail() {
        OperatingSystem os = info.getOperatingSystem(); // 获取操作系统信息
        HardwareAbstractionLayer hardware = info.getHardware();
        double memory = hardware.getMemory().getTotal() / 1024.0 / 1024 / 1024; //返回的是字节
        double diskSize = Arrays.stream(File.listRoots()).mapToLong(File::getTotalSpace).sum() / 1024.0 / 1024 / 1024; // 使用JDK8的流API计算磁盘大小
        String ip = Objects.requireNonNull(this.findNetworkInterface(hardware)).getIPv4addr()[0];

        return new BaseDetail()
                .setOsArch(properties.getProperty("os.arch"))
                .setOsName(os.getFamily())
                .setOsVersion(os.getVersionInfo().getVersion())
                .setOsBit(os.getBitness())
                .setCpuName(hardware.getProcessor().getProcessorIdentifier().getName())
                .setCpuCore(hardware.getProcessor().getLogicalProcessorCount()) //逻辑核心数
                .setMemory(memory)
                .setDisk(diskSize)
                .setIp(ip);
    }

    // 获取网卡信息, IP
    private NetworkIF findNetworkInterface(HardwareAbstractionLayer hardware) {
        try {
            for (NetworkIF network : hardware.getNetworkIFs()) {
                String[] ipv4Addr = network.getIPv4addr();
                NetworkInterface ni = network.queryNetworkInterface();
                if (!ni.isLoopback() && !ni.isPointToPoint() && ni.isUp() && !ni.isVirtual()
                        && (ni.getName().startsWith("eth") || ni.getName().startsWith("en")) && ipv4Addr.length > 0) {
                    return network;
                }
            }
        } catch (IOException e) {
            log.info("读取网络接口信息时出错", e);
        }

        return null;

    }

}









