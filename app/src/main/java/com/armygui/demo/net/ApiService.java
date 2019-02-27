package com.armygui.demo.net;



import com.armygui.demo.bean.Token;
import com.lookbi.baselib.bean.BaseBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface ApiService {

    //获取Member信息
    @GET(HttpUrl.TOKEN)
    Observable<BaseBean<Token>> getToken(@Query("token") String token);
}