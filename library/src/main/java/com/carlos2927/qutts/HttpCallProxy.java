package com.carlos2927.qutts;

public class HttpCallProxy {
    private HttpCall call;
    private BaseApi api;
    public HttpCallProxy setApi(BaseApi api){
        this.api = api;
        return this;
    }

    public BaseApi getApi(){
        return api;
    }
    void setCall(HttpCall call){
        this.call = call;
    }

    public HttpCall getCall(){
        return call;
    }

    public boolean hasCall(){
        return call != null;
    }
}
