package com.lookbi.baselib.lisenter;

/**
 * Created by Administrator on 2017/2/26.
 */

public interface OnOkHttpCallBack {
    //没有网络
    void onNoNetWork();

    //请求前
    void requestBefore();

    //请求发送失败
    void onHttpError();

    //请求成功
    void onSuccess(String result);

}
