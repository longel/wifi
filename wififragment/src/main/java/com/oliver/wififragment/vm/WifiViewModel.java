package com.oliver.wififragment.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.oliver.sdk.WifiAdmin;
import com.oliver.sdk.constant.Global;
import com.oliver.sdk.event.ConnectionEvent;
import com.oliver.sdk.event.RSSIEvent;
import com.oliver.sdk.event.SupplicantStateEvent;
import com.oliver.sdk.event.WifiEvent;
import com.oliver.sdk.event.WifiStateEvent;
import com.oliver.sdk.model.WifiHotspot;
import com.oliver.sdk.receiver.WifiReceiver;
import com.oliver.sdk.util.LogUtils;
import com.oliver.wififragment.constant.Constants;
import com.oliver.wififragment.event.ScanResultEvent;
import com.oliver.wififragment.model.AccessPoint;

import java.util.ArrayList;
import java.util.List;


/**
 * author : Oliver
 * date   : 2019/8/25
 * desc   :
 */

public class WifiViewModel extends AndroidViewModel {

    private static final String TAG = "WifiViewModel";
    private MutableLiveData<RSSIEvent> mRSSIEventLiveData;
    private MutableLiveData<WifiStateEvent> mWifiStateEventLiveData;
    private MutableLiveData<ScanResultEvent> mScanResultEventLiveData;
    private MutableLiveData<ConnectionEvent> mConnectionStateEventLiveData;
    private MutableLiveData<SupplicantStateEvent> mSupplicantStateEventLiveData;
    private WifiReceiver mWifiReceiver;
    private Context mContext;


    private Handler mHandler = new Handler();
    private Runnable mScanRunnable = new Runnable() {
        @Override
        public void run() {
            WifiAdmin.get().getScanResult(mCallback);
        }
    };

    private WifiAdmin.OnScanResultCallback mCallback = new WifiAdmin.OnScanResultCallback() {
        @Override
        public void onScanResult(List<WifiHotspot> wifiHotspots) {
            handleScanResults(wifiHotspots);
        }
    };

    private WifiReceiver.OnWifiStateChangeListener mWifiStateChangeListener = new WifiReceiver.OnWifiStateChangeListener() {
        @Override
        public void onWifiStateChange(WifiEvent stateEvent) {
            handleWifiStateEvent(stateEvent);
        }
    };

    public WifiViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        mRSSIEventLiveData = new MutableLiveData<>();
        mWifiStateEventLiveData = new MutableLiveData<>();
        mScanResultEventLiveData = new MutableLiveData<>();
        mConnectionStateEventLiveData = new MutableLiveData<>();
        mSupplicantStateEventLiveData = new MutableLiveData<>();
        start();
    }

    private void start() {
        registerWifiReceiver();
        if (WifiAdmin.get().isWifiEnable()) {
            WifiAdmin.get().scan();
        }
    }

    public LiveData<RSSIEvent> getRSSIEventLiveData() {
        return mRSSIEventLiveData;
    }

    public LiveData<WifiStateEvent> getWifiStateEventLiveData() {
        return mWifiStateEventLiveData;
    }

    public MutableLiveData<ScanResultEvent> getScanResultEventLiveData() {
        return mScanResultEventLiveData;
    }

    public MutableLiveData<ConnectionEvent> getConnectionStateEventLiveData() {
        return mConnectionStateEventLiveData;
    }

    public MutableLiveData<SupplicantStateEvent> getSupplicantStateEventLiveData() {
        return mSupplicantStateEventLiveData;
    }

    private void registerWifiReceiver() {
        mWifiReceiver = new WifiReceiver();
        mWifiReceiver.addOnWifiStateChangeListener(mWifiStateChangeListener);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mContext.registerReceiver(mWifiReceiver, filter);
    }

    public void unregisterWifiReceiver() {
        if (mWifiReceiver != null) {
            mWifiReceiver.removeOnWifiStateChangeListener(mWifiStateChangeListener);
            mContext.unregisterReceiver(mWifiReceiver);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        unregisterWifiReceiver();
        if (mHandler != null) {
            mHandler.removeCallbacks(mScanRunnable);
        }
        mHandler = null;
    }

    private void handleWifiStateEvent(WifiEvent wifiStateEvent) {
        if (wifiStateEvent == null) {
            return;
        }
        int updateId = wifiStateEvent.getUpdateId();
        switch (updateId) {
            case WifiEvent.WIFI_STATE_UPDATE:
                int wifiState = wifiStateEvent.getWifiState();
                WifiStateEvent stateEvent = new WifiStateEvent();
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        stateEvent.setState(Global.WIFI_STATE_DISABLED);
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        stateEvent.setState(Global.WIFI_STATE_DISABLING);
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        stateEvent.setState(Global.WIFI_STATE_ENABLING);
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        stateEvent.setState(Global.WIFI_STATE_ENABLED);
                        break;
                }
                syncState();
                mWifiStateEventLiveData.setValue(stateEvent);

                break;
            case WifiEvent.SCAN_RESULT_UPDATE:
                if (mHandler != null) {
                    mHandler.removeCallbacks(mScanRunnable);
                    mHandler.postDelayed(mScanRunnable, 500);
                }
                break;
            case WifiEvent.NETWORK_STATE_UPDATE:
                NetworkInfo networkInfo = wifiStateEvent.getNetworkInfo();
                if (networkInfo == null) {
                    return;
                }
                ConnectionEvent connectionEvent = new ConnectionEvent();
                if (networkInfo.getState() == NetworkInfo.State.DISCONNECTED) {
                    connectionEvent.setConnectionState(Global.DISCONNECTED);
                } else if (networkInfo.getState() == NetworkInfo.State.CONNECTING) {
                    connectionEvent.setConnectionState(Global.CONNECTING);
                } else if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    connectionEvent.setConnectionState(Global.CONNECTED);
                }
                mConnectionStateEventLiveData.setValue(connectionEvent);
                break;
            case WifiEvent.RSSI_UPDATE:
                mRSSIEventLiveData.setValue(new RSSIEvent());
                break;
            case WifiEvent.SUPPLICANT_STATE_UPDATE: // 该回调可能会回调多次
                WifiInfo wifiInfo = wifiStateEvent.getWifiInfo();
                if (wifiInfo != null && wifiInfo.getSupplicantState() != SupplicantState.DISCONNECTED) {
                    return;
                }
                SupplicantStateEvent supplicantStateEvent = new SupplicantStateEvent();
                supplicantStateEvent.setWifiInfo(wifiInfo);
                mSupplicantStateEventLiveData.setValue(supplicantStateEvent);
                break;
        }
    }

    public void syncState() {
        handleScanResults(null);
    }

    private void handleScanResults(List<WifiHotspot> datas) {
        List<AccessPoint> accessPoints = new ArrayList<>();
        if (datas != null && datas.size() > 0) {
            for (WifiHotspot hotspot : datas) {
                AccessPoint accessPoint = copyFromHotSpot(hotspot);
                accessPoints.add(accessPoint);
            }
        }
        AccessPoint accessPoint = new AccessPoint();
        accessPoint.setItemLayoutType(Constants.LAYOUT_TYPE_HEADER);
        accessPoint.setWifiEnable(WifiAdmin.get().isWifiEnable());
        accessPoints.add(0, accessPoint);
        ScanResultEvent event = new ScanResultEvent();
        event.setDatas(accessPoints);
        mScanResultEventLiveData.postValue(event);
    }

    private AccessPoint copyFromHotSpot(WifiHotspot hotspot) {
        AccessPoint accessPoint = new AccessPoint();
        accessPoint.setBssid(hotspot.getBssid());
        accessPoint.setSsid(hotspot.getSsid());
        accessPoint.setPassword(hotspot.getPassword());
        accessPoint.setNetworkId(hotspot.getNetworkId());
        accessPoint.setStrength(hotspot.getStrength());
        accessPoint.setConnection(hotspot.getConnection());
        accessPoint.setEncryption(hotspot.getEncryption());
        accessPoint.setLevel(hotspot.getLevel());
        accessPoint.setCapabilities(hotspot.getCapabilities());
        accessPoint.setLinkSpeed(hotspot.getLinkSpeed());
        accessPoint.setFrequency(hotspot.getFrequency());
        accessPoint.setIpAddress(hotspot.getIpAddress());
        accessPoint.setGateway(hotspot.getGateway());
        accessPoint.setNetmask(hotspot.getNetmask());
        accessPoint.setDns1(hotspot.getDns1());
        accessPoint.setDns2(hotspot.getDns2());
        accessPoint.setServerAddress(hotspot.getServerAddress());
        if (hotspot.isConnected() || hotspot.isConnecting()) {
            accessPoint.setItemLayoutType(Constants.LAYOUT_TYPE_ACTIVE);
        } else {
            accessPoint.setItemLayoutType(Constants.LAYOUT_TYPE_INACTIVE);
        }
        accessPoint.setWifiEnable(WifiAdmin.get().isWifiEnable());
        return accessPoint;
    }

    public boolean isSame(WifiHotspot hotspot, WifiInfo wifiInfo) {
        return WifiAdmin.get().isSame(hotspot, wifiInfo);
    }

    public void connect(WifiHotspot hotspot) {
        connect(hotspot, false);
    }

    public void connect(WifiHotspot hotspot, boolean remove) {
        boolean connect = WifiAdmin.get().connect(hotspot, remove);
        LogUtils.d(TAG, "connect operation result：" + connect);
    }

    public void connect(int networkId) {
        boolean connect = WifiAdmin.get().connect(networkId);
        LogUtils.d(TAG, "connect operation result：" + connect);
    }
}
