package com.carlos2927.qutts;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class Qutts {
    public final static String TAG = "Qutts";
    public static boolean IsDebug = false;
    public static final String Version = "v1.0.2";

    public static <R> HttpCallProxy asyncRequest(BaseApi api,@NonNull HttpRequestCallback<R> httpRequestCallback){
        if(!api.checkInvalid()){
            HttpCallProxy httpCallProxy = new HttpCallProxy().setApi(api);
            httpRequestCallback.onError(httpCallProxy,new RuntimeException(api.getClass().getName()+".checkInvalid() return false!!!"));
            return httpCallProxy;
        }
        api.workOn(QuttsSchedulers.CurrentThread);
        api.notifyOn(QuttsSchedulers.AndroidMainThread);
        return api.getHttpEngine().asyncRequest(api,httpRequestCallback).setApi(api);
    }

    public static <R> R request(BaseApi api,@Nullable HttpRequestCallback<R> httpRequestCallback){
        if(!api.checkInvalid()){
            RuntimeException e = new RuntimeException(api.getClass().getName()+".checkInvalid() return false!!!");
            if(httpRequestCallback != null){
                HttpCallProxy httpCallProxy = new HttpCallProxy();
                httpRequestCallback.onError(httpCallProxy,e);
            }else {
                if(IsDebug){
                    Log.e(TAG,e.getMessage());
                }
            }
            return null;
        }
        api.workOn(QuttsSchedulers.CurrentThread);
        api.notifyOn(QuttsSchedulers.CurrentThread);
        return api.getHttpEngine().request(api,httpRequestCallback);
    }

    public static <R> HttpCallProxy sendRequest(BaseApi api,@Nullable HttpRequestCallback<R> httpRequestCallback){
        HttpCallProxy httpCallProxy = new HttpCallProxy().setApi(api);
        if(!api.checkInvalid()){
            httpRequestCallback.onError(httpCallProxy,new RuntimeException(api.getClass().getName()+".checkInvalid() return false!!!"));
            return httpCallProxy;
        }
        if(api.getWorkScheduler() != null && api.getNotifyScheduler() != null){
            api.getHttpEngine().sendRequest(api,httpRequestCallback);
        }
        return httpCallProxy;
    }
}
