package com.lookbi.baselib.lisenter;

/**
 * Created by Administrator on 2017/2/26.
 */

public interface OnHttpLisenter<T> {
    //请求发送成功，但数据错误
    void onHttpError(String e);
    //请求成功
    void onSuccess(T mData);
    //无数据或数据size=0
    void onNoData();
}
