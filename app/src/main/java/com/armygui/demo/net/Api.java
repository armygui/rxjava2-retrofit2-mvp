package com.armygui.demo.net;


import com.lookbi.baselib.net.RetrofitFactory;

/*
 * 描述:     TODO 基础网络访问
 */
public class Api extends RetrofitFactory {

    private static Api api = new Api(HttpUrl.BASEURL);


    public Api(String baseUrl) {
        super(baseUrl);
    }


    public static ApiService getService() {
        return api.getRetrofit().create(ApiService.class);
    }

    public static <T> T getService(Class<T> service) {
        return api.getRetrofit().create(service);
    }


}
