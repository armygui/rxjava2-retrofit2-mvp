package com.lookbi.baselib.base;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by zhangyisheng on 2017/6/28.
 */

public interface IBasePresenter {
    void setShowLoading(boolean isShow);

    boolean isShowLoading();
    void setRefershOrLoadmore(boolean isShow);

    boolean isRefershOrLoadmore();

    //将网络请求的每一个disposable添加进入CompositeDisposable，再退出时候一并注销
    void addDisposable(Disposable subscription);

    //注销所有请求
    void unDisposable();


}
