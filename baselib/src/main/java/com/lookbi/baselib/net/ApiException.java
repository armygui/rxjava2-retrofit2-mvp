package com.lookbi.baselib.net;

/*
 * 描述:     TODO 自定义exception
 */
public class ApiException extends RuntimeException {

    private int status;

    public ApiException(Throwable throwable, int status) {
        super(throwable);
        this.status = status;
    }

    public ApiException(String message) {
        super(new Throwable(message));

    }
}