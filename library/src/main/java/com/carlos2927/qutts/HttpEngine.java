package com.carlos2927.qutts;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface HttpEngine {
    @Keep
    public static enum Type{
        /**
         * 采用系统原生HttpUrlConnection Api实现http引擎
         */
        Default,
        /**
         * 采用第三方Retrofit2框架实现http引擎
         */
        Retrofit2
    }


    HttpEngine newHttpEngine();

    /**
     * 同步网络请求
     * @param api
     * @param httpRequestCallback
     * @param <R>
     * @return
     */
    <R> R request(BaseApi api, @Nullable HttpRequestCallback<R> httpRequestCallback);

    /**
     * 异步网络请求
     * @param api
     * @param httpRequestCallback
     * @param <R>
     * @return
     */
    <R> HttpCallProxy asyncRequest(BaseApi api, @NonNull HttpRequestCallback<R> httpRequestCallback);

    /**
     * 发送网络请求(同步异步都可以)
     * @param api
     * @param httpRequestCallback
     * @param <R>
     * @return
     */
    <R> HttpCallProxy sendRequest(BaseApi api, @NonNull HttpRequestCallback<R> httpRequestCallback);


}
