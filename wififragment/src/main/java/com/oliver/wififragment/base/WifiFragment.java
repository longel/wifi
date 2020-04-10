package com.oliver.wififragment.base;

import android.arch.lifecycle.Observer;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiInfo;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.oliver.sdk.WifiAdmin;
import com.oliver.wififragment.event.ConnectionEvent;
import com.oliver.wififragment.event.SupplicantStateEvent;
import com.oliver.wififragment.event.WifiStateEvent;
import com.oliver.sdk.model.WifiHotspot;
import com.oliver.sdk.util.LogUtils;
import com.oliver.wififragment.R;
import com.oliver.wififragment.adapter.WifiAdapter;
import com.oliver.wififragment.constant.Constants;
import com.oliver.wififragment.event.ScanResultEvent;
import com.oliver.wififragment.model.AccessPoint;
import com.oliver.wififragment.vm.WifiViewModel;
import com.oliver.wififragment.widget.ConnectDialogBuilder;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.List;

/**
 * author : Oliver
 * date   : 2019/8/25
 * desc   :
 */

public class WifiFragment extends BaseFragment {

    private RecyclerView rvWifi;
    private ImageView ivBack;
    private TextView tvTitle;
    private WifiAdapter mWifiAdapter;

    private WifiViewModel mViewModel;

    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;


    public static WifiFragment newInstance() {
        return new WifiFragment();
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.fragment_wifi;
    }

    @Override
    protected void beforeOnCreate() {
        super.beforeOnCreate();
        mViewModel = new WifiViewModel(mActivity.getApplication());
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        ivBack = findView(R.id.wifi_iv_back);
        tvTitle = findView(R.id.wifi_tv_title);
        rvWifi = findView(R.id.wifi_rv);
        mWifiAdapter = new WifiAdapter();
        DividerItemDecoration decoration = new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(new ColorDrawable(Color.GRAY));
        rvWifi.addItemDecoration(decoration);
        rvWifi.setLayoutManager(new LinearLayoutManager(mActivity));
        mWifiAdapter.bindToRecyclerView(rvWifi);
    }


    @Override
    protected void initListener() {
        super.initListener();
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d(TAG, "click back!");
            }
        });
        mWifiAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                LogUtils.d(TAG, "setOnItemChildClickListener" + adapter.getItem(position));
                if (view.getId() == R.id.iv_wifi_switch) {
                    if (WifiAdmin.get().isWifiEnable()) {
                        WifiAdmin.get().disableWifi();
                    } else {
                        WifiAdmin.get().enableWifi(true);
                    }
                } else if (view.getId() == R.id.iv_inactive_wifi_enter) {
                    showDetailFragment(position);
                } else if (view.getId() == R.id.iv_active_wifi_enter) {
                    showDetailFragment(position);
                }
            }
        });

        mWifiAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LogUtils.d(TAG, "setOnItemClickListener" + adapter.getItem(position));
                AccessPoint accessPoint = mWifiAdapter.getItem(position);
                if (accessPoint == null) {
                    return;
                }
                if (accessPoint.getItemType() == Constants.LAYOUT_TYPE_HEADER) {
                    return;
                }
                if (accessPoint.isConnected()) { // 已连接
                    // TODO 弹出二维码分享密码
                    return;
                }
                if (accessPoint.isConfigured()) {
                    // 直接连接
                    mViewModel.connect(accessPoint.getNetworkId());
                } else {
                    showConnectDialog(accessPoint, false);
                }
            }
        });

        mViewModel.getWifiStateEventLiveData().observe(this, new Observer<WifiStateEvent>() {
            @Override
            public void onChanged(@Nullable WifiStateEvent wifiStateEvent) {
                if (wifiStateEvent == null) {
                    return;
                }
            }
        });
        mViewModel.getConnectionStateEventLiveData().observe(this, new Observer<ConnectionEvent>() {
            @Override
            public void onChanged(@Nullable ConnectionEvent wifiStateEvent) {

            }
        });

        mViewModel.getScanResultEventLiveData().observe(this, new Observer<ScanResultEvent>() {
            @Override
            public void onChanged(@Nullable ScanResultEvent scanResultEvent) {
                if (scanResultEvent == null) {
                    return;
                }
                mWifiAdapter.setNewData(scanResultEvent.getDatas());
            }
        });

        mViewModel.getSupplicantStateEventLiveData().observe(this, new Observer<SupplicantStateEvent>() {
            @Override
            public void onChanged(@Nullable SupplicantStateEvent supplicantStateEvent) {
                if (supplicantStateEvent == null) {
                    return;
                }
                WifiInfo wifiInfo = supplicantStateEvent.getWifiInfo();
                // 密码错误的时候 SupplicantState 状态值是 SupplicantState.DISCONNECTED
                if (wifiInfo == null) {
                    Toast.makeText(mActivity, "连接Wifi出错", Toast.LENGTH_SHORT).show();
                    return;
                }
                AccessPoint targetAccessPoint = null;
                List<AccessPoint> accessPoints = mWifiAdapter.getData();
                for (AccessPoint accessPoint : accessPoints) {
                    if (mViewModel.isSame(accessPoint, wifiInfo)) {
                        targetAccessPoint = accessPoint;
                        break;
                    }
                }
                LogUtils.e(TAG, "targetAccessPoint：" + targetAccessPoint);
                if (targetAccessPoint != null) {
                    showConnectDialog(targetAccessPoint, true);
                } else {
                    Toast.makeText(mActivity, "连接Wifi出错", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDetailFragment(int position) {
        DetailFragment detailFragment = DetailFragment.newInstance();
        detailFragment.setAccessPoint(mWifiAdapter.getItem(position));
        detailFragment.show(getChildFragmentManager(), detailFragment.getClass().getSimpleName());
    }

    private QMUIDialog qmuiDialog;
    private ConnectDialogBuilder mConnectDialogBuilder;

    private void showConnectDialog(WifiHotspot wifiHotspot, boolean isFail) {
        ConnectDialogBuilder connectDialog = new ConnectDialogBuilder(mActivity);
        connectDialog.setHotspot(wifiHotspot);
        connectDialog.setFailed(isFail);
        final QMUIDialog qmuiDialog = connectDialog.create(mCurrentDialogStyle);
        connectDialog.setOnClickDialogChildListener(new ConnectDialogBuilder.OnClickDialogChildListener() {
            @Override
            public void onCancel(WifiHotspot hotspot) {
                qmuiDialog.dismiss();
            }

            @Override
            public void onConnect(WifiHotspot hotspot, boolean isFailed) {
                qmuiDialog.dismiss();
                // 连接Wifi
                mViewModel.connect(hotspot, isFailed);
            }
        });
        qmuiDialog.show();
        QMUIKeyboardHelper.showKeyboard(connectDialog.getEtPassword(), true);
    }

    @Override
    protected void initData() {
        super.initData();
        mViewModel.syncState();
    }

}
