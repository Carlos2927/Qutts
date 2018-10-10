package com.carlos2927.qutts;

import java.util.Map;

public interface BaseApi {
    boolean checkInvalid();
    void release();
    BaseApi newBaseApi();
    String getBaseUrl();
    BaseApi setBaseUrl(String baseUrl);
    String getApi();
    String getHttpMethod();
    BaseApi setHttpMethod(String httpMethod);
    BaseApi workOn(QuttsSchedulers.Schedulerable workScheduler);
    BaseApi notifyOn(QuttsSchedulers.Schedulerable notifyScheduler);
    QuttsSchedulers.Schedulerable getWorkScheduler();
    QuttsSchedulers.Schedulerable getNotifyScheduler();
    Map<String,String> getHeaders();
    BaseApi addHeader(String key, String value);
    BaseApi setHeaders(Map<String, String> headers);
    BaseApi setQueryParams(String queryParams);
    BaseApi addQueryParams(String key, String value);
    String getQueryParams();
    BaseApi setHttpEntity(HttpEntity httpEntity);
    HttpEntity getHttpEntity();
    String getHttpEngineType();
    BaseApi setHttpEngineType(String httpEngineType);
    BaseApi setCustomHttpEngine(HttpEngine httpEngine);
    HttpEngine getHttpEngine();
}
