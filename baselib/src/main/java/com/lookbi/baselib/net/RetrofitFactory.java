package com.lookbi.baselib.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.lookbi.baselib.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/*
 * 描述:     TODO
 */
public class RetrofitFactory implements IRetrofit {
    private static final long TIMEOUT = 15;
    private volatile static Retrofit retrofit = null;
    protected Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
    protected OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();

    public RetrofitFactory(String baseUrl) {
        retrofitBuilder
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpBuilder
                        .addInterceptor(new BaseUrlInterceptor())
                        .addInterceptor(getLoggerInterceptor())
                        .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                        .readTimeout(TIMEOUT, TimeUnit.SECONDS).build())
                .baseUrl(baseUrl);
    }

    /**
     * 构建retroft
     *
     * @return Retrofit对象
     */
    @Override
    public Retrofit getRetrofit() {
        if (retrofit == null) {
            //锁定代码块
            synchronized (RetrofitFactory.class) {
                if (retrofit == null) {
                    retrofit = retrofitBuilder.build(); //创建retrofit对象
                }
            }
        }
        return retrofit;

    }


    @Override
    public OkHttpClient.Builder setInterceptor(Interceptor interceptor) {
        return httpBuilder.addInterceptor(interceptor);
    }

    @Override
    public Retrofit.Builder setConverterFactory(Converter.Factory factory) {
        return retrofitBuilder.addConverterFactory(factory);
    }

    /**
     * 日志拦截器
     * 将你访问的接口信息
     *
     * @return 拦截器
     */
    public HttpLoggingInterceptor getLoggerInterceptor() {
        //日志显示级别
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
        //新建log拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String message) {
                try {
                    if (message.startsWith("{\"")) {
                        LogUtil.e(message);
                    } else {
                        if (message.startsWith("<--")||message.startsWith("-->")) {
                            LogUtil.e(message);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        loggingInterceptor.setLevel(level);
        return loggingInterceptor;
    }



}
