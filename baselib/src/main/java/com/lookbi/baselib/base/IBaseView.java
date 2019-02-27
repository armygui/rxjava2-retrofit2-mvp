package com.lookbi.baselib.base;

/**
 * Created by zhangyisheng on 2017/6/26.
 */

public interface IBaseView {

    //请求错误
    void httpError(String e);

    void noData(int code);

    void requestEnd();
}
