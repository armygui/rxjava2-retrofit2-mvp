package com.lookbi.baselib.lisenter;

import java.io.File;

/**
 * 鲁班压缩回调
 * Created by zhangyisheng on 2017/10/11.
 */

public interface OnLubanLisenter {
    void start();
    void success(File file);
    void fail();
}
