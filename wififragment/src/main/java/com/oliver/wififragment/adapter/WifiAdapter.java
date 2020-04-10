package com.oliver.wififragment.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.oliver.sdk.constant.Global;
import com.oliver.wififragment.R;
import com.oliver.wififragment.constant.Constants;
import com.oliver.wififragment.model.AccessPoint;

import java.util.List;

/**
 * author : Oliver
 * date   : 2019/8/23
 * desc   :
 */

public class WifiAdapter extends BaseMultiItemQuickAdapter<AccessPoint, BaseViewHolder> {

    public WifiAdapter() {
        this(null);
    }

    public WifiAdapter(List<AccessPoint> data) {
        super(data);
        addItemType(Constants.LAYOUT_TYPE_ACTIVE, R.layout.layout_wifi_active);
        addItemType(Constants.LAYOUT_TYPE_HEADER, R.layout.layout_wifi_switcher);
        addItemType(Constants.LAYOUT_TYPE_INACTIVE, R.layout.layout_wifi_inactive);
    }

    @Override
    protected void convert(BaseViewHolder helper, final AccessPoint item) {
        if (helper.getItemViewType() == Constants.LAYOUT_TYPE_HEADER) {
            ImageView ivSwitcher = helper.getView(R.id.iv_wifi_switch);
            if (item.hasWifiEnable()) {
                ivSwitcher.setSelected(true);
            } else {
                ivSwitcher.setSelected(false);
            }
            helper.setVisible(R.id.tv_enable_wifi_hint, !item.hasWifiEnable())
                    .addOnClickListener(R.id.iv_wifi_switch);
        } else if (helper.getItemViewType() == Constants.LAYOUT_TYPE_INACTIVE) {
            ImageView wifiIvIcon = helper.getView(R.id.iv_inactive_wifi_icon);
            updateWifiIcon(item, wifiIvIcon);
            helper.setText(R.id.tv_inactive_wifi_ssid, item.getSsid())
                    .setVisible(R.id.iv_inactive_wifi_lock, item.hasEncryption())
                    .addOnClickListener(R.id.iv_inactive_wifi_enter);

        } else if (helper.getItemViewType() == Constants.LAYOUT_TYPE_ACTIVE) {
            ImageView wifiIvIcon = helper.getView(R.id.iv_active_wifi_icon);
            updateWifiIcon(item, wifiIvIcon);
            helper.setText(R.id.tv_active_wifi_ssid, item.getSsid())
                    .setText(R.id.tv_active_wifi_hint, item.isConnected() ? "点击分享Wifi" : "正在连接Wifi")
                    .setVisible(R.id.iv_active_wifi_lock, item.hasEncryption())
                    .addOnClickListener(R.id.iv_active_wifi_enter);
        }
    }

    private void updateWifiIcon(AccessPoint item, ImageView wifiIvIcon) {
        if (item.getStrength() == Global.WEAK) {
            wifiIvIcon.setImageResource(R.mipmap.ic_wifi_level_1);
        } else if (item.getStrength() == Global.KIND) {
            wifiIvIcon.setImageResource(R.mipmap.ic_wifi_level_2);
        } else if (item.getStrength() == Global.NICE) {
            wifiIvIcon.setImageResource(R.mipmap.ic_wifi_level_3);
        } else if (item.getStrength() == Global.BEST) {
            wifiIvIcon.setImageResource(R.mipmap.ic_wifi_level_4);
        }
    }
}
