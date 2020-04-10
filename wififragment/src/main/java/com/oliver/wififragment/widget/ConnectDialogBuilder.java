package com.oliver.wififragment.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oliver.sdk.constant.Global;
import com.oliver.sdk.model.WifiHotspot;
import com.oliver.wififragment.R;
import com.qmuiteam.qmui.alpha.QMUIAlphaTextView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogBuilder;

/**
 * author : Oliver
 * date   : 2019/8/25
 * desc   :
 */

public class ConnectDialogBuilder extends QMUIDialogBuilder<ConnectDialogBuilder> {
    private Context mContext;
    private TextView tvTitle;
    private TextView tvFailTip;
    private EditText etPassword;
    private ImageView ivEye;
    private QMUIAlphaTextView tvCancel;
    private QMUIAlphaTextView tvConnect;
    private WifiHotspot mHotspot;
    private int limitLength;

    private boolean isFailed;

    private OnClickDialogChildListener mOnClickDialogChildListener;

    public ConnectDialogBuilder(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreateContent(QMUIDialog dialog, ViewGroup parent, Context context) {
        View layout = LayoutInflater.from(mContext).inflate(R.layout.layout_dialog_wifi_connect, null);
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        etPassword = layout.findViewById(R.id.et_dialog_password);
        etPassword.setFocusable(true);
        etPassword.setFocusableInTouchMode(true);
        etPassword.setImeOptions(EditorInfo.IME_ACTION_GO);
        etPassword.setId(com.qmuiteam.qmui.R.id.qmui_dialog_edit_input);

        tvTitle = layout.findViewById(R.id.tv_dialog_connect_title);
        tvFailTip = layout.findViewById(R.id.tv_dialog_fail_tip);
        tvCancel = layout.findViewById(R.id.tv_dialog_cancel);
        tvConnect = layout.findViewById(R.id.tv_dialog_connect);
        tvConnect.setChangeAlphaWhenPress(true);
        tvCancel.setChangeAlphaWhenDisable(true);
        tvCancel.setChangeAlphaWhenPress(true);
        tvConnect.setChangeAlphaWhenDisable(true);
        ivEye = layout.findViewById(R.id.iv_dialog_password_eye);

        if (etPassword.getText().toString().length() < limitLength) {
            tvConnect.setEnabled(false);
        } else {
            tvConnect.setEnabled(true);
        }

        if (isFailed) {
            tvFailTip.setVisibility(View.VISIBLE);
        } else {
            tvFailTip.setVisibility(View.GONE);
        }


        if (mHotspot != null) {
            tvTitle.setText(mHotspot.getSsid());
        }

        initListeners();

        parent.addView(layout);
    }

    private void initListeners() {
        ivEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivEye.setSelected(!ivEye.isSelected());
                if (ivEye.isSelected()) {
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD|InputType.TYPE_CLASS_TEXT);
                } else {
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                }
            }
        });

        etPassword.setKeyListener(new NumberKeyListener() {
            @NonNull
            @Override
            protected char[] getAcceptedChars() {
                return Global.PASSWORD_MATCHER.toCharArray();
            }

            @Override
            public int getInputType() {
                return InputType.TYPE_CLASS_TEXT;
            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null == s || s.toString().length() < limitLength) {
                    tvConnect.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != s && s.toString().length() >= limitLength) {
                    tvConnect.setEnabled(true);
                }
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mOnClickDialogChildListener != null) {
                    mOnClickDialogChildListener.onCancel(mHotspot);
                }
            }
        });
        tvConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickDialogChildListener != null) {
                    if (mHotspot != null) {
                        mHotspot.setPassword(etPassword.getText().toString());
                    }
                    mOnClickDialogChildListener.onConnect(mHotspot,isFailed);
                }
            }
        });
    }

    public Context getContext() {
        return mContext;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public EditText getEtPassword() {
        return etPassword;
    }

    public ImageView getIvEye() {
        return ivEye;
    }

    public TextView getTvCancel() {
        return tvCancel;
    }

    public TextView getTvConnect() {
        return tvConnect;
    }

    public WifiHotspot getHotspot() {
        return mHotspot;
    }

    public boolean isFailed() {
        return isFailed;
    }

    public void setFailed(boolean failed) {
        isFailed = failed;
    }

    public void setHotspot(WifiHotspot hotspot) {
        mHotspot = hotspot;
        limitLength = hotspot == null ? 8 : hotspot.getEncryption() == Global.WPA ? 8 : 5;
    }

    public void setOnClickDialogChildListener(OnClickDialogChildListener onClickDialogChildListener) {
        mOnClickDialogChildListener = onClickDialogChildListener;
    }

    public interface OnClickDialogChildListener {
        void onCancel(WifiHotspot hotspot);

        void onConnect(WifiHotspot hotspot,boolean failed);
    }


}
