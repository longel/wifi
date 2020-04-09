package com.oliver.wififragment.base;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oliver.sdk.R;
import com.oliver.sdk.WifiAdmin;
import com.oliver.wififragment.model.AccessPoint;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;

/**
 * author : Oliver
 * date   : 2019/8/25
 * desc   :
 */

public class DetailFragment extends DialogFragment {


    protected View mContentView;
    protected Context mContext;

    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    private RelativeLayout mWifiRlTop;
    private ImageView mWifiIvBack;
    private TextView mWifiTvTitle;
    private TextView mTvConnectionState;
    private TextView mTvStrength;
    private TextView mTvSpeed;
    private TextView mTvEncryption;
    private TextView mTvIp;
    private TextView mTvNetMask;
    private TextView mTvRouter;
    private QMUIAlphaButton mBtnModifyPassword;
    private QMUIAlphaButton mBtnDelete;

    private AccessPoint mAccessPoint;
    private boolean mIsShowing;

    public static DetailFragment newInstance() {
        
        Bundle args = new Bundle();
        
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE, R.style.OliverDialog);

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_wifi_detail, container, false);
        Dialog dialog = getDialog();
        dialog.setCanceledOnTouchOutside(true);
        return mContentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        bindListener();
        updateLayout();
    }


    public void setAccessPoint(AccessPoint accessPoint) {
        mAccessPoint = accessPoint;
    }

    protected void initView() {
        mWifiRlTop = findView(R.id.wifi_rl_top);
        mWifiIvBack = findView(R.id.wifi_iv_back);
        mWifiTvTitle = findView(R.id.wifi_tv_title);
        mTvConnectionState = findView(R.id.tv_connection_state);
        mTvStrength = findView(R.id.tv_strength);
        mTvSpeed = findView(R.id.tv_speed);
        mTvEncryption = findView(R.id.tv_encryption);
        mTvIp = findView(R.id.tv_ip);
        mTvNetMask = findView(R.id.tv_net_mask);
        mTvRouter = findView(R.id.tv_router);
        mBtnModifyPassword = findView(R.id.btn_modify_password);
        mBtnDelete = findView(R.id.btn_delete);
        if (mAccessPoint != null) {
            mWifiTvTitle.setText(mAccessPoint.getSsid());
        }
    }

    private void updateLayout() {
        if (mAccessPoint != null) {
            mTvConnectionState.setText(mAccessPoint.connectionState2String());
            mTvStrength.setText(mAccessPoint.strength2String());
            if (mAccessPoint.isConnected()) {
                mTvSpeed.setText(mAccessPoint.linkSpeed2String());
                mTvEncryption.setText(mAccessPoint.getCapabilities());
                mTvIp.setText(mAccessPoint.address2String());
                mTvNetMask.setText(mAccessPoint.netmask2String());
                mTvRouter.setText(mAccessPoint.gateway2String());
            }
        }
    }


    private void bindListener() {
        mWifiIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAccessPoint != null) {
                    boolean remove = WifiAdmin.get().remove(mAccessPoint.getNetworkId());
                    if (remove) {
                        Toast.makeText(mContext, "刪除网络成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "刪除网络失败", Toast.LENGTH_SHORT).show();
                    }
                    mWifiIvBack.performClick();
                } else {
                    mBtnDelete.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "刪除网络失败", Toast.LENGTH_SHORT).show();
                            mWifiIvBack.performClick();
                        }
                    },1000);
                }
            }
        });
    }

    protected <T extends View> T findView(@IdRes int id) {
        return mContentView.findViewById(id);
    }

    protected Drawable getDrawableById(@DrawableRes int resId) {
        if (resId == 0) {
            return null;
        }
        return getResources().getDrawable(resId);
    }

    @Override
    public void onStart() {
        super.onStart();

        setLayoutParams();
    }

    private void setLayoutParams() {
        Dialog dialog = getDialog();
        if (dialog == null) {
            return;
        }
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setDimAmount(0.7f);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = getScreenWidth(mContext);
        lp.height = getScreenHeight(mContext);
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
    }


    public static int getScreenWidth(Context context) {
        WindowManager manager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
        int w = 0;
        if (manager != null) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            Display display = manager.getDefaultDisplay();
            if (display != null) {
                display.getMetrics(outMetrics);
                w = outMetrics.widthPixels;
            }
        }
        return w;
    }

    public static int getScreenHeight(Context context) {
        WindowManager manager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
        int h = 0;
        if (manager != null) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            Display display = manager.getDefaultDisplay();
            if (display != null) {
                display.getMetrics(outMetrics);
                h = outMetrics.heightPixels;
            }
        }
        return h;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        if (!mIsShowing) {
            return;
        }
        mIsShowing = false;
        super.onDismiss(dialog);
        Log.d("ZDialog", "弹窗消失");
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (mIsShowing) {
            return;
        }
        mIsShowing = true;
        super.show(manager, tag);
    }

    public boolean isShowing() {
        return mIsShowing;
    }
}

