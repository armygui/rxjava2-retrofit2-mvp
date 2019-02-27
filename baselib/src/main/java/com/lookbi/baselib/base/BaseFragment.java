package com.lookbi.baselib.base;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gyf.barlibrary.ImmersionBar;
import com.gyf.barlibrary.ImmersionFragment;
import com.lookbi.baselib.base.BasePresenterImpl;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseFragment<V, P extends BasePresenterImpl<V>> extends ImmersionFragment {
    protected P mPresenter;
    protected Context mContext;
    protected Activity mActivity;
    protected View mRootView;
    protected boolean isVisible;
    private boolean isPrepared;
    private boolean isFirst = true;
    private Unbinder unbinder = null;
    protected final int ACCESS_FINE_LOCATION_REQUEST_CODE=1;
    protected final int READ_PHONE_STATE_REQUEST_CODE=2;
    protected final int CAMERA_REQUEST_CODE=3;
    protected final int CALL_PHONE_REQUEST_CODE=4;
    protected final int READ_EXTERNAL_STORAGE_REQUEST_CODE=5;
    public BaseFragment() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            lazyLoad();
        } else {
            isVisible = false;
            onInvisible();
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mActivity = getActivity();
//        setHasOptionsMenu(true);
        //判断是否使用MVP模式
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.attachView((V) this);//因为之后所有的子类都要实现对应的View接口
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(bindLayout(), container, false);
            unbinder = ButterKnife.bind(this, mRootView);
            initView();
        }
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isPrepared = true;
        lazyLoad();
    }

    protected void lazyLoad() {
        if (!isPrepared || !isVisible || !isFirst) {
            return;
        }
        initData();
        isFirst = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) mRootView.getParent()).removeView(mRootView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter=null;
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (unbinder != null) {
            unbinder.unbind();
            unbinder=null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mPresenter != null) {
            mPresenter.setShowLoading(false);
        }
    }

    protected <T extends View> T findView(int resId) {
        return (T) (mRootView.findViewById(resId));
    }

    protected <T extends View> T findView(View rootView, int resId) {
        return (T) (rootView.findViewById(resId));
    }

    protected void setOnClickLisenter(int resId, View.OnClickListener listener) {
        if (listener != null) {
            findView(resId).setOnClickListener(listener);
        }
    }

    protected void setOnClickLisenter(View viewparent, int resId, View.OnClickListener listener) {
        if (listener != null) {
            viewparent.findViewById(resId).setOnClickListener(listener);
        }
    }

    //用于创建Presenter和判断是否使用MVP模式(由子类实现)
    protected abstract P createPresenter();

    //得到当前界面的布局文件id(由子类实现)
    protected abstract int bindLayout();
    protected abstract int getFristTopViewId();

    public abstract void initView();

    public abstract void initData();

    protected void onInvisible() {

    }

    public void startActivity(Class activity) {
        Intent intent = new Intent(mActivity, activity);
        startActivity(intent);
    }

    public void startActivity(Class activity, Bundle bundle) {
        Intent intent = new Intent(mActivity, activity);
        intent.putExtra("data", bundle);
        startActivity(intent);
    }


    public void startActivityAndClearTask(Class activity) {
        Intent intent = new Intent(mActivity, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        mActivity.finish();
    }

    public void startActivityAndClearTop(Class activity) {
        Intent intent = new Intent(mActivity, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    /**
     * 检验 读取定位  权限
     *
     * @return
     */
    public boolean checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义)
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);

                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    /**
     * 检验 读取SD卡  权限
     *
     * @return
     */
    public boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义)
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    /**
     * 检验 读取手机信息  权限
     *
     * @return
     */
    public boolean checkPhoneStatePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义)
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{
                                Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_REQUEST_CODE);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    /**
     * 检验 请求相机 权限
     *
     * @return
     */
    public boolean checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义)
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{
                                Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    /**
     * 检验 请求打电话 权限
     *
     * @return
     */
    public boolean checkCallPhonePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义)
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{
                                Manifest.permission.CALL_PHONE}, CALL_PHONE_REQUEST_CODE);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
    @Override
    protected void immersionInit() {
        if (getFristTopViewId()!=0){
            ImmersionBar.with(this).statusBarDarkFont(true, 0.2f).titleBar(getFristTopViewId()).init();
        }
    }





}
