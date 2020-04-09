package com.oliver.sdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;

import com.oliver.sdk.action.ScanResultAction;
import com.oliver.sdk.constant.Global;
import com.oliver.sdk.model.WifiHotspot;
import com.oliver.sdk.util.LogUtils;
import com.oliver.sdk.util.SysUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * author : Oliver
 * date   : 2019/8/23
 * desc   :
 */

public class WifiAdmin {

    private static final String TAG = "WifiAdmin";
    private static final int DEFAULT_STRENGTH_LEVEL = 4;
    @SuppressWarnings("StaticFieldLeak")
    private static Context sContext;
    private WifiManager mWifiManager;
    private ConnectivityManager mConnectivityManager;
    private int mStrengthLevel = DEFAULT_STRENGTH_LEVEL;
    private boolean placeActiveWifi2First = true;
    private boolean filterWifiByEmptySSID = true;
    private boolean sortByHasConfigured = false; // 将已配置过的Wifi放在第一位

    private WifiAdmin() {
        mWifiManager = (WifiManager) sContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mConnectivityManager = (ConnectivityManager) sContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static WifiAdmin get() {
        check();
        return Holder.sInstance;
    }

    private static void check() {
        if (sContext == null) {
            throw new RuntimeException("please call init() first!");
        }
    }

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }


    private static final class Holder {
        @SuppressLint("StaticFieldLeak")
        private static final WifiAdmin sInstance = new WifiAdmin();
    }

    public boolean isWifiEnable() {
        return mWifiManager.isWifiEnabled();
    }

    public boolean enableWifi() {
        if (isWifiEnable()) {
            return true;
        }
        return mWifiManager.setWifiEnabled(true);
    }

    /**
     * 打开Wifi并扫描Wifi
     *
     * @param scan 是否扫描Wifi
     *
     * @return
     */
    public boolean enableWifi(boolean scan) {
        if (isWifiEnable()) {
            return true;
        }
        boolean enabled = mWifiManager.setWifiEnabled(true);
        if (enabled && scan) {
            scan();
        }
        return enabled;
    }

    public boolean disableWifi() {
        if (!isWifiEnable()) {
            return true;
        }
        return mWifiManager.setWifiEnabled(false);
    }

    public void scan() {
        if (SysUtil.isMinVersionP()) {
            LogUtils.e(TAG, "the method has deprecated over Android P!");
        }
        mWifiManager.startScan();
    }

    public boolean isScanAlwaysAvailable() {
        return mWifiManager.isScanAlwaysAvailable();
    }


    public boolean disconnect() {
        return mWifiManager.disconnect();
    }

    /**
     * forget a ever connected hotspot
     */
    public boolean remove(int networkId) {
        /**
         * remove operation always fails above api 21,so we try many times
         */
        boolean isRemoved = mWifiManager.removeNetwork(networkId);
        if (!isRemoved) {
            int index = 0;
            while (!isRemoved && index < 10) {
                index++;
                isRemoved = mWifiManager.removeNetwork(networkId);
            }
        }
        if (isRemoved) {
            mWifiManager.saveConfiguration();
        }
        LogUtils.d(TAG, "remove: " + isRemoved);
        return isRemoved;
    }

    public boolean connect(int networkId) {
        LogUtils.d(TAG, "connect networkId：" + networkId);
        return mWifiManager.enableNetwork(networkId, true);
    }


    public boolean connect(WifiHotspot hotspot) {
        return connect(hotspot, false);
    }

    /**
     * 关于密码错误的wifi，需要将wifi的配置清除掉，不然不能触发底层的广播逻辑。具体原因未跟踪代码，不详
     *
     * @param hotspot
     * @param remove
     *
     * @return
     */
    public boolean connect(WifiHotspot hotspot, boolean remove) {
        LogUtils.d(TAG, "connect remove：" + remove);
        LogUtils.d(TAG, "connect hotspot：" + hotspot);
        if (remove && hotspot != null) {
            remove(hotspot.getNetworkId());
        }
        WifiConfiguration config = createConfiguration(hotspot);
        if (config == null) {
            LogUtils.e(TAG, "can't create a config !");
            return false;
        }

        int networkId = mWifiManager.addNetwork(config);
        if (networkId < 0) {
            return false;
        }

        if (mWifiManager.enableNetwork(networkId, true)) {
            return true;
        }
        if (mWifiManager.saveConfiguration()) {
            if (mWifiManager.reconnect()) {
                return true;
            }
        }
        return false;
    }


    public WifiConfiguration createConfiguration(WifiHotspot hotspot) {
        if (hotspot == null) {
            return null;
        }
        String password = hotspot.getPassword();
        String SSID = hotspot.getSsid();
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = addQuote(SSID);

        int encryption = hotspot.getEncryption();
        switch (encryption) {
            case Global.WEP:
                int i = password.length();
                if (((i == 10 || (i == 26) || (i == 58))) && (password.matches("[0-9A-Fa-f]*"))) {
                    config.wepKeys[0] = password;
                } else {
                    config.wepKeys[0] = addQuote(password);
                }
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
                break;
            case Global.WPA:
                config.preSharedKey = addQuote(password);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                break;
            default:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        return config;
    }


    /**
     * 判断该ScanResult是否连接过且连接配置还未被清除
     *
     * @return -1不存在该ScanResult配置
     */
    public int getConfigId(ScanResult scanResult) {
        List<WifiConfiguration> configurations = getConfiguredWifi();
        if (configurations == null || configurations.size() <= 0) {
            LogUtils.d("连接配置为空！");
            return -1;
        }

        for (WifiConfiguration configuration : configurations) {
            /**
             * The network's SSID. Can either be a UTF-8 string,
             * which must be enclosed in double quotation marks
             * (e.g., {@code "MyNetwork"}), or a string of
             * hex digits, which are not enclosed in quotes
             * (e.g., {@code 01a243f405}).
             */
            if (removeQuote(configuration.SSID).equals(scanResult.SSID)) {
                return configuration.networkId;
            }
        }
        return -1;
    }

    public int getConfigId(WifiHotspot hotspot) {
        List<WifiConfiguration> configurations = getConfiguredWifi();
        if (configurations == null || configurations.size() <= 0) {
            LogUtils.d("连接配置为空！");
            return -1;
        }

        for (WifiConfiguration configuration : configurations) {
            /**
             * The network's SSID. Can either be a UTF-8 string,
             * which must be enclosed in double quotation marks
             * (e.g., {@code "MyNetwork"}), or a string of
             * hex digits, which are not enclosed in quotes
             * (e.g., {@code 01a243f405}).
             */
            if (removeQuote(configuration.SSID).equals(hotspot.getSsid())) {
                return configuration.networkId;
            }
        }
        return -1;
    }

    public boolean isConfigured(WifiHotspot hotspot) {
        return getConfigId(hotspot) != -1;
    }

    public boolean isConfigured(ScanResult result) {
        return getConfigId(result) != -1;
    }

    public String addQuote(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        return "\"" + text + "\"";
    }

    public String removeQuote(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        if (!text.contains("\"")) {
            return text;
        }
        return text.replace("\"", "").trim();
    }

    public static String removeDoubleQuotes(String string) {
        if (string == null)
            return null;
        final int length = string.length();
        if ((length > 1) && (string.charAt(0) == '"') && (string.charAt(length - 1) == '"')) {
            return string.substring(1, length - 1);
        }
        return string;
    }


    /**
     * 获取存在连接配置的wifi
     */
    public List<WifiConfiguration> getConfiguredWifi() {
        return mWifiManager.getConfiguredNetworks();
    }


    public void getScanResult() {
        getScanResult(true);
    }

    public void getScanResult(boolean sort) {
        getScanResult(sort, true);
    }

    public void getScanResult(final boolean sort, final boolean descend) {
        Disposable disposable = Observable
                .create(new ObservableOnSubscribe<List<ScanResult>>() {
                    @Override
                    public void subscribe(ObservableEmitter<List<ScanResult>> emitter) {
                        if (!emitter.isDisposed()) {
                            List<ScanResult> scanResults = mWifiManager.getScanResults();
                            if (sort) {
                                if (descend) {
                                    Collections.sort(scanResults, descendComparator);
                                } else {
                                    Collections.sort(scanResults, ascendComparator);
                                }
                            }
                            if (scanResults == null) {
                                emitter.onComplete();
                                return;
                            }
                            emitter.onNext(scanResults);
                            emitter.onComplete();
                        }
                    }
                })
                .map(new Function<List<ScanResult>, List<WifiHotspot>>() {
                    @Override
                    public List<WifiHotspot> apply(List<ScanResult> scanResults) throws Exception {
                        List<WifiHotspot> wifiHotspots = new ArrayList<>();
                        if (scanResults != null && scanResults.size() > 0) {
                            WifiHotspot connectedWifi = null;
                            LogUtils.d(TAG, "getActiveWifi: " + getActiveWifi());
                            LogUtils.d(TAG, "getActiveNetworkInfo: " + getActiveNetworkInfo());
                            for (ScanResult scanResult : scanResults) {

                                /**
                                 * 过滤掉没有名字的Wifi
                                 */
                                if (filterWifiByEmptySSID && TextUtils.isEmpty(scanResult.SSID.trim())) {
                                    continue;
                                }
                                WifiHotspot hotspot = new WifiHotspot();
                                hotspot.setBssid(scanResult.BSSID);
                                hotspot.setSsid(scanResult.SSID);
                                hotspot.setPassword("");
                                hotspot.setLevel(scanResult.level);
                                hotspot.setStrength(transStrength(scanResult));
                                hotspot.setEncryption(transCapability(scanResult));
                                hotspot.setConnection(transConnectionState(scanResult));
                                hotspot.setCapabilities(scanResult.capabilities);
                                hotspot.setNetworkId(getConfigId(scanResult));
                                if (placeActiveWifi2First
                                        && (hotspot.isConnected() || hotspot.isConnecting())) {
                                    connectedWifi = hotspot;
                                } else {
                                    wifiHotspots.add(hotspot);
                                }
                                WifiInfo wifiInfo = getActiveWifi();
                                if (hotspot.isConnected() && wifiInfo != null) {
                                    hotspot.setFrequency(wifiInfo.getFrequency());
                                    hotspot.setLinkSpeed(wifiInfo.getLinkSpeed());
                                }
                                DhcpInfo dhcpInfo = getDhcpInfo();
                                if (hotspot.isConnected() && dhcpInfo != null) {
                                    hotspot.setDns1(dhcpInfo.dns1);
                                    hotspot.setDns2(dhcpInfo.dns2);
                                    hotspot.setGateway(dhcpInfo.gateway);
                                    hotspot.setIpAddress(dhcpInfo.ipAddress);
                                    hotspot.setNetmask(dhcpInfo.netmask);
                                    hotspot.setServerAddress(dhcpInfo.serverAddress);
                                }
                            }
                            if (connectedWifi != null) {
                                wifiHotspots.add(0, connectedWifi);
                            }
                        }
                        return wifiHotspots;
                    }
                })
                .map(new Function<List<WifiHotspot>, List<WifiHotspot>>() {
                    @Override
                    public List<WifiHotspot> apply(List<WifiHotspot> wifiHotspots) {
                        if (wifiHotspots != null && wifiHotspots.size() > 0) {
                            if (sortByHasConfigured) {
                                return sortedByHasConfigured(wifiHotspots);
                            }
                        }
                        return wifiHotspots;
                    }
                })
                .subscribeOn(Schedulers.single())
                .subscribe(new Consumer<List<WifiHotspot>>() {
                    @Override
                    public void accept(List<WifiHotspot> wifiHotspots) {
                        ScanResultAction resultEvent = new ScanResultAction();
                        resultEvent.setDatas(wifiHotspots);
                        EventBus.getDefault().post(resultEvent);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    public List<WifiHotspot> sortedByHasConfigured(List<WifiHotspot> wifiHotspots) {
        List<WifiHotspot> configuredList = new ArrayList<>();
        List<WifiHotspot> unconfiguredList = null;
        if (wifiHotspots == null || wifiHotspots.size() == 0) {
            return null;
        }
        for (WifiHotspot hotspot : wifiHotspots) {
            if (isConfigured(hotspot)) {
                configuredList.add(hotspot);
            } else {
                if (unconfiguredList == null) {
                    unconfiguredList = new ArrayList<>();
                }
                unconfiguredList.add(hotspot);
            }
        }
        if (unconfiguredList != null) {
            configuredList.addAll(unconfiguredList);
        }
        return configuredList;
    }

    private int transStrength(ScanResult scanResult) {
        if (scanResult == null) {
            return Global.WEAK;
        }
        return transStrength(scanResult.level);
    }

    private int transStrength(int level) {
        int signalLevel = WifiManager.calculateSignalLevel(level, mStrengthLevel);
        if (signalLevel == mStrengthLevel - 1) {
            return Global.BEST;
        }
        if (signalLevel == mStrengthLevel - 2) {
            return Global.NICE;
        }
        if (signalLevel == mStrengthLevel - 3) {
            return Global.KIND;
        }
        return Global.WEAK;
    }

    public int calculateStrength(int level, int numLevels) {
        return WifiManager.calculateSignalLevel(level, numLevels);
    }

    public int calculateStrengthByDefault(int level) {
        return WifiManager.calculateSignalLevel(level, mStrengthLevel);
    }

    public int transConnectionState(ScanResult scanResult) {
        if (scanResult == null) {
            return Global.DISCONNECTED;
        }
        WifiInfo wifiInfo = getActiveWifi();
        NetworkInfo networkInfo = getActiveNetworkInfo();
        //        LogUtils.d(TAG, "getActiveWifi: " + wifiInfo);
        //        LogUtils.d(TAG, "getActiveNetworkInfo: " + networkInfo);
        //        LogUtils.d(TAG, "========================================================================");
        if (networkInfo != null && wifiInfo != null) { // 连接成功
            if (isSame(scanResult, wifiInfo)) {
                return Global.CONNECTED;
            }
        } else if (wifiInfo != null) {
            if (isSame(scanResult, wifiInfo)) {
                return Global.CONNECTING;
            }
        }
        return Global.DISCONNECTED;
    }

    /**
     * @param scanResult SSID: oliver_5G  // 没有双引号
     * @param wifiInfo   SSID："oliver_5G"  // 带有双引号
     *
     * @return true--同一个热点
     */
    private boolean isSame(ScanResult scanResult, WifiInfo wifiInfo) {
        return scanResult != null
                && wifiInfo != null
                && TextUtils.equals(scanResult.BSSID, wifiInfo.getBSSID())
                && TextUtils.equals(addQuote(scanResult.SSID), wifiInfo.getSSID());
    }

    /**
     * @param hotspot  SSID: oliver_5G  // 没有双引号
     * @param wifiInfo SSID："oliver_5G"  // 带有双引号
     *
     * @return true--同一个热点
     */
    public boolean isSame(WifiHotspot hotspot, WifiInfo wifiInfo) {
        //        LogUtils.e(TAG, "hotspot.getBssid()：" + hotspot.getBssid());
        //        LogUtils.e(TAG, "wifiInfo.getBSSID()：" + wifiInfo.getBSSID());
        //        LogUtils.e(TAG, "hotspot.getSsid()：" + hotspot.getSsid());
        //        LogUtils.e(TAG, "wifiInfo.getSSID()：" + wifiInfo.getSSID());

        return hotspot != null
                && wifiInfo != null
                && TextUtils.equals(addQuote(hotspot.getSsid()), wifiInfo.getSSID());
    }

    public int transCapability(ScanResult scanResult) {
        if (scanResult == null) {
            return Global.NONE;
        }
        return transCapability(scanResult.capabilities);
    }

    public int transCapability(String capability) {
        if (TextUtils.isEmpty(capability)) {
            return Global.NONE;
        }
        if (capability.toLowerCase().contains("wep")) {
            return Global.WEP;
        }
        if (capability.toLowerCase().contains("wpa")) {
            return Global.WPA;
        }
        return Global.NONE;
    }

    /**
     * 当前有Wifi活动就有返回值。也就是说，不管是wifi连接成功或失败，都能取到值
     *
     * @return
     */
    public WifiInfo getActiveWifi() {
        return mWifiManager.getConnectionInfo();
    }


    public DhcpInfo getDhcpInfo() {
        return mWifiManager.getDhcpInfo();
    }

    /**
     * 只有连接成功才能有值
     *
     * @return
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private NetworkInfo getActiveNetworkInfo() {
        return mConnectivityManager == null ? null : mConnectivityManager.getActiveNetworkInfo();
    }

    public boolean hasConnectedNetwork() {
        return mConnectivityManager != null
                && mConnectivityManager.getActiveNetworkInfo() != null;
    }


    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public boolean isWifiConnected() {
        return mConnectivityManager != null
                && mConnectivityManager.getActiveNetworkInfo() != null
                && mConnectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }


    private Comparator<? super ScanResult> descendComparator = new Comparator<ScanResult>() {
        @Override
        public int compare(ScanResult o1, ScanResult o2) {
            return o2.level - o1.level;
        }
    };

    private Comparator<? super ScanResult> ascendComparator = new Comparator<ScanResult>() {
        @Override
        public int compare(ScanResult o1, ScanResult o2) {
            return o1.level - o2.level;
        }
    };
}
