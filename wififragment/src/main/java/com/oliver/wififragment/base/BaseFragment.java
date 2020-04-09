package com.oliver.wififragment.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * author : Oliver
 * date   : 2019/8/25
 * desc   :
 */

public abstract class BaseFragment extends Fragment {

    public static String TAG;

    protected Activity mActivity;
    protected View rootView;

    public void onAttach(Context context) {
        TAG = getClass().getSimpleName();
        this.mActivity = (Activity) context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        beforeOnCreate();
        super.onCreate(savedInstanceState);
        afterOnCreate();
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("AbsBaseFragment", "rootView:" + this.rootView);
        if (this.rootView == null) {
            this.rootView = inflater.inflate(this.provideContentViewId(), container, false);
            this.initView(this.rootView);
            this.initListener();
        } else {
            ViewGroup parent = (ViewGroup) this.rootView.getParent();
            if (this.rootView.getParent() != null) {
                parent.removeView(this.rootView);
            }
        }

        return this.rootView;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.initData();
    }

    public void onDetach() {
        super.onDetach();
        this.mActivity = null;
    }

    protected void initData() {
    }

    protected void beforeOnCreate() {
    }

    protected void afterOnCreate() {
    }

    protected void initListener() {
    }

    protected void initView(View view) {
    }

    protected <T extends View> T findView(@IdRes int id) {
        return this.rootView.findViewById(id);
    }

    protected boolean isViewVisible(View view) {
        return view != null && view.getVisibility() == 0;
    }

    protected abstract int provideContentViewId();

    public void post(Runnable runnable) {
        if (this.mActivity != null) {
            this.mActivity.runOnUiThread(runnable);
        }

    }

    public void onDestroy() {
        super.onDestroy();
        this.rootView = null;
    }


}
