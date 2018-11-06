package com.carlos2927.qutts;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public interface HttpRequestCallback<R> {
    void onStart();
    void onDataUploading(long uploadedCount, long dataLen);
    void onDownloading(long downloadedCount, long dataLen);
    boolean onInterceptRequest(HttpCallProxy call);
    void onReceivedResponseHeaders(HttpCallProxy call, Map<String, List<String>> headers);
    Type getResultType();
    R convert(HttpCallProxy call, Object object);
    void onError(HttpCallProxy call, Throwable e);
    void onSuccess(HttpCallProxy call, R result);
    boolean isSucceed(R result);
}
