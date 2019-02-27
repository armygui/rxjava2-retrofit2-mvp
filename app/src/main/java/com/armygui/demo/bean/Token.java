package com.armygui.demo.bean;

import java.io.Serializable;


public class Token implements Serializable {
    private long failuretime;//失效时间
    private String token;    //token

    public long getFailuretime() {
        return failuretime;
    }

    public void setFailuretime(long failuretime) {
        this.failuretime = failuretime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
