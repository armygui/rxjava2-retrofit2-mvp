package com.lookbi.baselib.base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;

import com.gyf.barlibrary.ImmersionBar;
import com.lookbi.baselib.R;
import com.lookbi.baselib.utils.ActivityManager;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public abstract class BaseActivity<V, P extends BasePresenterImpl<V>> extends
        AppCompatActivity {
    private View mContentView = null;
    private Unbinder unbinder = null;
    protected ImmersionBar mImmersionBar;
    protected P mPresenter;
    protected final int ACCESS_FINE_LOCATION_REQUEST_CODE = 0x11;
    protected final int READ_PHONE_STATE_REQUEST_CODE = 0x12;
    protected final int CAMERA_REQUEST_CODE = 0x13;
    protected final int CALL_PHONE_REQUEST_CODE = 0x14;
    protected final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 0x15;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActivityManager.getAppInstance().addActivity(this);//将当前activity添加进入管理栈
        //子类不再需要设置布局ID，也不再需要使用ButterKnife.bind()
        if (bindLayout() != 0) {
            mContentView = LayoutInflater.from(this)
                    .inflate(bindLayout(), null);
        }
        if (mContentView != null) {
            setContentView(mContentView);
        }
        //判断是否使用MVP模式
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.attachView((V) this);//因为之后所有的子类都要实现对应的View接口
        }
        unbinder = ButterKnife.bind(this);
        mImmersionBar = ImmersionBar.with(this);
//        mImmersionBar.init();
        setStatus();
        initView();
        initData(savedInstanceState);
        initData();
    }

    private void setStatus() {
        if (getFristTopViewId() != 0) {
            mImmersionBar.statusBarDarkFont(true, 0.2f)
                    .titleBar(getFristTopViewId())
                    .init();
        }
    }


    @Override
    protected void onDestroy() {
        unDisposable();
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
        if (mImmersionBar != null) {
            mImmersionBar.destroy();
            mImmersionBar = null;
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        mContentView = null;
        ActivityManager.getAppInstance().removeActivity(this);
        super.onDestroy();
//        System.gc();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPresenter != null) {
            mPresenter.setShowLoading(false);
        }
    }

    //用于创建Presenter和判断是否使用MVP模式(由子类实现)
    protected abstract P createPresenter();

    //得到当前界面的布局文件id(由子类实现)
    protected abstract int bindLayout();

    //沉浸式用到的布局id
    protected abstract int getFristTopViewId();

    protected abstract void initView();


    protected void initData(Bundle savedInstanceState) {

    }

    protected abstract void initData();


    //将此PresenterImpl中所有正在处理的Subscription都添加到CompositeSubscription中。统一退出的时候注销观察
    private CompositeDisposable mCompositeDisposable;

    /**
     * 添加Disposable
     *
     * @param subscription
     */
    public void addDisposable(Disposable subscription) {
        //csb 如果解绑了的话添加 sp 需要新的实例否则绑定时无效的
        if (mCompositeDisposable == null || mCompositeDisposable.isDisposed()) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(subscription);
    }

    /**
     * 在界面退出等需要解绑观察者的情况下调用此方法统一解绑，防止Rx造成的内存泄漏
     */
    public void unDisposable() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
            mCompositeDisposable = null;
        }
    }


    public void startActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    public void startActivity(Class activity, Bundle bundle) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("data", bundle);
        startActivity(intent);
    }

    /**
     * Activity跳转
     *
     * @param activity
     * @param action
     * @return
     */
    public Intent setIntents(Class activity, String... action) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("action", action);
        startActivity(intent);
        return intent;
    }

    public String[] getArrayAction() {
        return getIntent().getStringArrayExtra("action");
    }

    public void startActivityAndClearTask(Class activity) {
        Intent intent = new Intent(this, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void startActivityAndClearTop(Class activity) {
        Intent intent = new Intent(this, activity);
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
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义)
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_FINE_LOCATION_REQUEST_CODE);

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
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义)
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXTERNAL_STORAGE_REQUEST_CODE);
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
            if (this.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义)
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_PHONE_STATE},
                        READ_PHONE_STATE_REQUEST_CODE);
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
            if (this.checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义)
                ActivityCompat.requestPermissions(this,
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
            if (this.checkSelfPermission(Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义)
                ActivityCompat.requestPermissions(this,
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
}
