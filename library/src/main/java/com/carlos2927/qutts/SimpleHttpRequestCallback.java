package com.carlos2927.qutts;

import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;


public abstract class SimpleHttpRequestCallback<R> implements HttpRequestCallback<R> {
    Type resultType;

    public SimpleHttpRequestCallback(){
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        resultType =  params[0];
    }

    @Override
    public void onDataUploading(long uploadedCount, long dataLen) {
        if(dataLen > uploadedCount && uploadedCount>=0){
            if(Qutts.IsDebug){
                Log.i(Qutts.TAG,String.format("onDataUploading:%.2f%%(%s/%s)", uploadedCount * 100f / dataLen, uploadedCount, dataLen));
            }
        }
    }

    @Override
    public void onReceivedResponseHeaders(HttpCallProxy call, Map<String, List<String>> headers) {

    }

    @Override
    public boolean onInterceptRequest(HttpCallProxy call) {
        return false;
    }

    @Override
    public Type getResultType() {
        return resultType;
    }

    @Override
    public void onDownloading(long downloadedCount, long dataLen) {
        if(dataLen > downloadedCount && downloadedCount>=0){
            if(Qutts.IsDebug){
                Log.i(Qutts.TAG,String.format("onDownloading:%.2f%%(%s/%s)",downloadedCount*100f/dataLen,downloadedCount,dataLen));
            }
        }
    }

    @Override
    public void onError(HttpCallProxy call, Throwable e) {
        if(e != null){
            if(Qutts.IsDebug){
                String msg = e.getMessage();
                Log.e(Qutts.TAG, TextUtils.isEmpty(msg)?e.toString():e.getClass().getName()+"/r/n"+msg);
            }
        }
    }




    @Override
    public boolean isSucceed(R result) {
        return result != null;
    }
}
