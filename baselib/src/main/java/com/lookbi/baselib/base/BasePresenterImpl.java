package com.lookbi.baselib.base;


import android.app.Activity;

import com.lookbi.baselib.utils.LogUtil;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class BasePresenterImpl<V> implements IBasePresenter {

    /*================== 以下是网络请求接口 ==================*/

    public Activity mContext;
    private boolean isShowLoading = true;
    private boolean isRefershOrLoadmore = false;

    public BasePresenterImpl(Activity context) {
        mContext = context;
    }

    private Reference<V> mWeakRef;

    public void attachView(V view) {
        mWeakRef = new WeakReference<V>(view);
    }

    //请求完 先判断是否连接和View层连接  否则可能会空指针
    public boolean isViewAttached() {
        return mWeakRef != null && mWeakRef.get() != null;
    }

    public void detachView() {
        if (mWeakRef != null) {
            mWeakRef.clear();
            mWeakRef = null;
        }
        unDisposable();
    }

    public V getView() {
        return mWeakRef != null ? mWeakRef.get() : null;
    }


    //将此PresenterImpl中所有正在处理的Subscription都添加到CompositeSubscription中。统一退出的时候注销观察
    private CompositeDisposable mCompositeDisposable;

    @Override
    public void setShowLoading(boolean isShowLoading) {
        this.isShowLoading = isShowLoading;
    }

    @Override
    public boolean isShowLoading() {
        return isShowLoading;
    }

    @Override
    public void setRefershOrLoadmore(boolean isRefershOrLoadmore) {
        this.isRefershOrLoadmore=isRefershOrLoadmore;
    }
    /**
     * 是否是下拉刷新-上拉加载 状态
     * @return
     */
    @Override
    public boolean isRefershOrLoadmore() {
        return isRefershOrLoadmore;
    }



    /**
     * 添加Disposable
     *
     * @param subscription
     */
    @Override
    public void addDisposable(Disposable subscription) {
        //csb 如果解绑了的话添加 sb 需要新的实例否则绑定时无效的
        if (mCompositeDisposable == null || mCompositeDisposable.isDisposed()) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(subscription);
    }

    /**
     * 在界面退出等需要解绑观察者的情况下调用此方法统一解绑，防止Rx造成的内存泄漏
     */
    @Override
    public void unDisposable() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
            mCompositeDisposable = null;
        }
    }


}
