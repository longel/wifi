package com.oliver.sdk.vm;

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
import android.support.annotation.NonNull;

import com.oliver.sdk.WifiAdmin;
import com.oliver.sdk.action.ScanAction;
import com.oliver.sdk.action.ScanResultAction;
import com.oliver.sdk.constant.Global;
import com.oliver.sdk.event.ConnectionEvent;
import com.oliver.sdk.event.RSSIEvent;
import com.oliver.sdk.event.ScanResultEvent;
import com.oliver.sdk.event.SupplicantStateEvent;
import com.oliver.sdk.event.WifiEvent;
import com.oliver.sdk.event.WifiStateEvent;
import com.oliver.sdk.model.AccessPoint;
import com.oliver.sdk.model.WifiHotspot;
import com.oliver.sdk.receiver.WifiReceiver;
import com.oliver.sdk.util.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    private List<AccessPoint> mDatas;

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
        registerEventBus();
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

    private void registerEventBus() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void unregisterEventBus() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void registerWifiReceiver() {
        mWifiReceiver = new WifiReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mContext.registerReceiver(mWifiReceiver, filter);
    }

    public void unregisterWifiReceiver() {
        mContext.unregisterReceiver(mWifiReceiver);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        unregisterEventBus();
        unregisterWifiReceiver();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBlueEvent(WifiEvent wifiStateEvent) {
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
                ScanAction scanEvent = new ScanAction();
                WifiAdmin.get().getScanResult(scanEvent);
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
        onScanResult(new ScanAction());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScanResult(ScanResultAction<AccessPoint> scanResultEvent) {
        if (scanResultEvent == null) {
            return;
        }
        List<AccessPoint> datas = scanResultEvent.getDatas();
        LogUtils.d(TAG, "onScanResult: " + datas);
        if (datas == null) {
            datas = new ArrayList<>();
        }
        AccessPoint accessPoint = new AccessPoint();
        accessPoint.setItemLayoutType(AccessPoint.LAYOUT_TYPE_HEADER);
        accessPoint.setWifiEnable(WifiAdmin.get().isWifiEnable());
        datas.add(0, accessPoint);
        ScanResultEvent event = new ScanResultEvent();
        event.setDatas(datas);
        mScanResultEventLiveData.setValue(event);
    }

    public boolean isSame(WifiHotspot hotspot, WifiInfo wifiInfo) {
        return WifiAdmin.get().isSame(hotspot, wifiInfo);
    }

    public void connect(WifiHotspot hotspot) {
        connect(hotspot, false);
    }

    public void connect(WifiHotspot hotspot, boolean remove) {
        boolean connect = WifiAdmin.get().connect(hotspot,remove);
        LogUtils.d(TAG, "connect operation result：" + connect);
    }

    public void connect(int networkId) {
        boolean connect = WifiAdmin.get().connect(networkId);
        LogUtils.d(TAG, "connect operation result：" + connect);
    }
}
