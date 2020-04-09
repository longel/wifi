package com.oliver.sdk.model;

import android.net.wifi.WifiInfo;

import com.oliver.sdk.annotations.Connection;
import com.oliver.sdk.annotations.Encryption;
import com.oliver.sdk.annotations.Strength;
import com.oliver.sdk.constant.Global;
import com.oliver.sdk.util.NetworkUtils;

import java.util.Locale;

import static com.oliver.sdk.constant.Global.CONNECTED;
import static com.oliver.sdk.constant.Global.CONNECTING;
import static com.oliver.sdk.constant.Global.NONE;

/**
 * author : Oliver
 * date   : 2019/8/24
 * desc   :
 */

public class WifiHotspot {

    private String bssid;   // 对于手机端来说，就是MAC地址
    private String ssid;    // wifi名称
    private String password;// wifi密码
    private int networkId;  // 已连接的wifi，有networkId
    private int strength;   // 信号强度
    private int connection; // 连接状态
    private int encryption; // 加密类型
    /**
     * wifi的原始信号，可根据该值自定义wifi显示信号的计算，也可调用
     * {@link com.oliver.sdk.WifiAdmin#calculateStrength(int, int)} 来计算
     * <pre>
     *     默认不使用,通过使用4级信号来计算并赋值给strength字段使用
     * </pre>
     */
    private int level;
    /**
     * 加密类型信息
     */
    private String capabilities;

    private int linkSpeed = -1;
    private int frequency = -1;
    public int ipAddress;
    public int gateway;
    public int netmask;
    public int dns1;
    public int dns2;
    public int serverAddress;


    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid == null ? "" : bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid == null ? "" : ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? "" : password;
    }

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    @Strength
    public int getStrength() {
        return strength;
    }

    public void setStrength(@Strength int strength) {
        this.strength = strength;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities == null ? "" : capabilities;
    }

    public int getLinkSpeed() {
        return linkSpeed;
    }

    public void setLinkSpeed(int linkSpeed) {
        this.linkSpeed = linkSpeed;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(int ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getGateway() {
        return gateway;
    }

    public void setGateway(int gateway) {
        this.gateway = gateway;
    }

    public int getNetmask() {
        return netmask;
    }

    public void setNetmask(int netmask) {
        this.netmask = netmask;
    }

    public int getDns1() {
        return dns1;
    }

    public void setDns1(int dns1) {
        this.dns1 = dns1;
    }

    public int getDns2() {
        return dns2;
    }

    public void setDns2(int dns2) {
        this.dns2 = dns2;
    }

    public int getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(int serverAddress) {
        this.serverAddress = serverAddress;
    }

    public boolean isConfigured() {
        return getNetworkId() != -1;
    }

    public boolean isConnected() {
        return getConnection() == CONNECTED;
    }

    public boolean isConnecting() {
        return getConnection() == CONNECTING;
    }

    @Connection
    public int getConnection() {
        return connection;
    }

    public void setConnection(@Connection int connection) {
        this.connection = connection;
    }

    @Encryption
    public int getEncryption() {
        return encryption;
    }

    public void setEncryption(@Encryption int encryption) {
        this.encryption = encryption;
    }

    public boolean hasEncryption() {
        return getEncryption() != NONE;
    }


    @Override
    public String toString() {
        return "WifiHotspot{" +
                "bssid='" + bssid + '\'' +
                ", ssid='" + ssid + '\'' +
                ", password='" + password + '\'' +
                ", networkId=" + networkId +
                ", strength=" + strength +
                ", connection=" + connection +
                ", encryption=" + encryption +
                ", level=" + level +
                ", capabilities='" + capabilities + '\'' +
                '}';
    }

    public String get2String() {
        return "bssid='" + bssid + '\'' +
                ", ssid='" + ssid + '\'' +
                ", password='" + password + '\'' +
                ", networkId=" + networkId + '\'' +
                ", strength=" + strength + '\'' +
                ", connection=" + connection + '\'' +
                ", encryption=" + encryption + '\'' +
                ", level=" + level + '\'' +
                ", capabilities='" + capabilities + '\'';
    }


    public String connectionState2String() {
        String text = "未连接";
        switch (getConnection()) {
            case Global.CONNECTED:
                text = "已连接";
                break;
            case Global.CONNECTING:
                text = "连接中";
                break;
            case Global.DISCONNECTED:
                text = "未连接";
                break;
        }
        return text;
    }

    public String strength2String() {
        String text = "微弱";
        switch (getStrength()) {
            case Global.BEST:
                text = "极佳";
                break;
            case Global.NICE:
                text = "良好";
                break;
            case Global.KIND:
                text = "一般";
                break;
            case Global.WEAK:
                text = "微弱";
                break;
        }
        return text;
    }


    public String linkSpeed2String() {
        return String.format(Locale.getDefault(), "%d%s", getLinkSpeed(), WifiInfo.LINK_SPEED_UNITS);
    }

    public String address2String() {
        return NetworkUtils.getAddress(getIpAddress());
    }

    public String gateway2String() {
        return NetworkUtils.getAddress(getGateway());
    }

    public String netmask2String() {
        return NetworkUtils.getAddress(getNetmask());
    }

}
