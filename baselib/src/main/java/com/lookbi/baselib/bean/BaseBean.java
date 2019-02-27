package com.lookbi.baselib.bean;

import java.io.Serializable;

/**
 * Created by ZYS on 2017/4/22.
 */

public class BaseBean<T> implements Serializable {
    private String error;
    private T data;
    private int status;

    public boolean isSuccess() {
        return status != 0;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseBean{" +
                "error='" + error + '\'' +
                ", data=" + data +
                '}';
    }
}
