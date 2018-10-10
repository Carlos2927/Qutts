package com.carlos2927.qutts;

import java.util.HashMap;

public class HttpEngineFactory {
    private final static HashMap<String,HttpEngine> DefaultHttpEngineMap = new HashMap<>();
    private final static HashMap<String,HttpEngine> CustomHttpEngineMap = new HashMap<>();
    static {
        DefaultHttpEngineMap.put(HttpEngine.Type.Default.name(),new DefaultHttpEngine());
//        DefaultHttpEngineMap.put(HttpEngine.Type.Retrofit2.name(),null);
    }

    public static void registerCustomHttpEngine(String httpEngineType, HttpEngine httpEngine){
        CustomHttpEngineMap.put(httpEngineType,httpEngine);
    }

    public static HttpEngine create(String httpEngineType, boolean isCreateNew){
        HttpEngine httpEngine = CustomHttpEngineMap.get(httpEngineType);
        if(httpEngine == null){
            httpEngine = DefaultHttpEngineMap.get(httpEngineType);
        }
        if(httpEngine != null){
            return isCreateNew?httpEngine.newHttpEngine():httpEngine;
        }
        return null;
    }
}
