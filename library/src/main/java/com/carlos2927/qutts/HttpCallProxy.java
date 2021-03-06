package com.carlos2927.qutts;

public class HttpCallProxy {
    private HttpCall call;
    private BaseApi api;
    private boolean isConvertError;
    public HttpCallProxy setApi(BaseApi api){
        this.api = api;
        return this;
    }

    public BaseApi getApi(){
        return api;
    }
    protected void setCall(HttpCall call){
        this.call = call;
    }

    public HttpCall getCall(){
        return call;
    }

    public boolean hasCall(){
        return call != null;
    }

    /**
     * HttpRequestCallback.convert()发生错误时调用HttpRequestCallback.onError()处理，并调用此方法
     */
    public void convertError(){
        isConvertError = true;
    }

    public boolean isConvertError(){
        return isConvertError;
    }


}
