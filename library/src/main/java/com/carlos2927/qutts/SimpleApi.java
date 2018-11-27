package com.carlos2927.qutts;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public  abstract class SimpleApi implements BaseApi {
    public static class HttpMethod

    {
        public static final String POST = "POST";
        public static final String GET = "GET";
        public static final String PUT = "PUT";
        public static final String PATCH = "PATCH";
        public static final String DELETE = "DELETE";
        public static final String MOVE = "MOVE";// WebDAV
        public static final String PROPPATCH = "PROPPATCH";// WebDAV
        public static final String REPORT = "REPORT";// CalDAV/CardDAV (defined in WebDAV Versioning)

    }
    protected Builder mBuilder;
    public static class Builder{
        String api;
        String baseUrl;
        String httpMethod;
        String httpEngineType = HttpEngine.Type.Default.name();
        private Builder(){}
        public static Builder create(String api, String httpMethod, String baseUrl){
            Builder builder = new Builder();
            builder.api = api;
            builder.httpMethod = httpMethod;
            builder.baseUrl = baseUrl;
            return builder;
        }

        public static Builder create(String httpMethod, String baseUrl){
            Builder builder = new Builder();
            builder.httpMethod = httpMethod;
            builder.baseUrl = baseUrl;
            return builder;
        }

        public SimpleApi build(SimpleApi simpleApi){
            simpleApi.mBuilder = this;
            return simpleApi;
        }

        public Builder deepClone(){
            return Builder.create(api,httpMethod,baseUrl);
        }


    }



    protected Map<String, String> headers;
    protected StringBuilder queryParams;
    protected HttpEntity httpEntity;

    protected HttpEngine httpEngine;
    protected QuttsSchedulers.Schedulerable workScheduler;
    protected QuttsSchedulers.Schedulerable notifyScheduler;
    @Override
    public BaseApi setCustomHttpEngine(HttpEngine httpEngine) {
        this.httpEngine = httpEngine;
        return this;
    }

    @Override
    public BaseApi workOn(QuttsSchedulers.Schedulerable workScheduler) {
        this.workScheduler = workScheduler;
        return this;
    }

    @Override
    public QuttsSchedulers.Schedulerable getNotifyScheduler() {
        return notifyScheduler;
    }

    @Override
    public QuttsSchedulers.Schedulerable getWorkScheduler() {
        return workScheduler;
    }

    @Override
    public BaseApi notifyOn(QuttsSchedulers.Schedulerable notifyScheduler) {
        this.notifyScheduler = notifyScheduler;
        return this;
    }

    @Override
    public HttpEngine getHttpEngine() {
        if(httpEngine ==  null && mBuilder!= null){
            httpEngine = HttpEngineFactory.create(mBuilder.httpEngineType,true);
        }
        return httpEngine;
    }

    @Override
    public String getHttpEngineType() {
        return mBuilder.httpEngineType;
    }

    @Override
    public BaseApi setHttpEngineType(String httpEngineType) {
        mBuilder.httpEngineType = httpEngineType;
        return this;
    }

    @Override
    public boolean checkInvalid() {
        return mBuilder != null && !TextUtils.isEmpty(mBuilder.httpMethod) && !TextUtils.isEmpty(mBuilder.baseUrl) && getHttpEngine() != null;
    }

    @Override
    public void release() {
        if(headers != null){
            headers.clear();
        }
        headers = null;
        queryParams = null;
        if(httpEntity != null){
            httpEntity.fileHttpBodyList.clear();
            httpEntity.normalHttpBodyList.clear();
        }
        httpEntity = null;
        notifyScheduler = null;
        workScheduler = null;
    }

//    @Override
//    public BaseApi newBaseApi() {
//        return null;
//    }

    @Override
    public String getBaseUrl() {
        return mBuilder.baseUrl;
    }

    @Override
    public BaseApi setBaseUrl(String baseUrl) {
        mBuilder.baseUrl = baseUrl;
        return this;
    }

    @Override
    public String getApi() {
        return mBuilder.api;
    }

    @Override
    public String getHttpMethod() {
        return mBuilder.httpMethod;
    }

    @Override
    public BaseApi setHttpMethod(String httpMethod) {
        mBuilder.httpMethod = httpMethod;
        return this;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public BaseApi setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public BaseApi addHeader(String key, String value) {
        if(headers == null){
            headers = new HashMap<>();
        }
        headers.put(key,value);
        return this;
    }

    @Override
    public BaseApi setQueryParams(String queryParams) {
        this.queryParams = new StringBuilder(queryParams);
        return this;
    }



    @Override
    public BaseApi addQueryParams(String key, String value) {
        if(TextUtils.isEmpty(queryParams)){
            queryParams = new StringBuilder();
        }
        if(queryParams.length()!=0){
            queryParams.append('&');
        }
        queryParams.append(key);
        queryParams.append('=');
        try {
            queryParams.append(URLEncoder.encode(value,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public String getQueryParams() {
        return queryParams!=null?queryParams.toString():null;
    }

    @Override
    public BaseApi setHttpEntity(HttpEntity httpEntity) {
        this.httpEntity = httpEntity;
        return this;
    }

    @Override
    public BaseApi setJsonParams(String jsonParams) {
        return setHttpEntity(HttpEntity.create(StringBody.createJsonStringBody(jsonParams)));
    }

    @Override
    public HttpEntity getHttpEntity() {
        return httpEntity;
    }
}
