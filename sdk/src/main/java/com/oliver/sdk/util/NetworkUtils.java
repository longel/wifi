package com.oliver.sdk.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * author : Oliver
 * date   : 2019/8/25
 * desc   :
 */

public class NetworkUtils {


    /**
     * 获取的是ipv4的Ip地址
     *
     * @return
     */
    public static String getAddress(int hostAddress) {
        int iPv4Address = getIPv4Address(getAddressByte(hostAddress));
        return numericToTextFormat(getRealIPvAddress(iPv4Address));
    }

    private static byte[] getRealIPvAddress(int address) {
        byte[] addr = new byte[4];
        addr[0] = (byte) ((address >>> 24) & 0xFF);
        addr[1] = (byte) ((address >>> 16) & 0xFF);
        addr[2] = (byte) ((address >>> 8) & 0xFF);
        addr[3] = (byte) (address & 0xFF);
        return addr;
    }

    private static String numericToTextFormat(byte[] src) {
        return (src[0] & 0xff) + "." + (src[1] & 0xff) + "." + (src[2] & 0xff) + "." + (src[3] & 0xff);
    }

    private static byte[] getAddressByte(int hostAddress) {
        return new byte[]{(byte) (0xff & hostAddress),
                (byte) (0xff & (hostAddress >> 8)),
                (byte) (0xff & (hostAddress >> 16)),
                (byte) (0xff & (hostAddress >> 24))};
    }

    private static int getIPv4Address(byte[] addr) {
        int address = addr[3] & 0xFF;
        address |= ((addr[2] << 8) & 0xFF00);
        address |= ((addr[1] << 16) & 0xFF0000);
        address |= ((addr[0] << 24) & 0xFF000000);
        return address;
    }
}
