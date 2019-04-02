package com.carlos2927.qutts;

import android.os.Build;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 采用系统原生HttpUrlConnection Api实现http引擎
 */
public class DefaultHttpEngine implements HttpEngine {
    private static final ExecutorService executorService = new ThreadPoolExecutor(8, 64, 60, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable runnable) {
            Thread result = new Thread(runnable, "Qutts-DefaultHttpEngine-Dispatcher"){
                @Override
                public void run() {
                    setPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    super.run();
                }
            };
            result.setDaemon(false);
            return result;
        }
    });
    @Override
    public HttpEngine newHttpEngine() {
        return new DefaultHttpEngine();
    }

    @Override
    public <R> R request(BaseApi api, @Nullable HttpRequestCallback<R> httpRequestCallback) {
        HttpCallProxy httpCallProxy = new HttpCallProxy();
        httpCallProxy.setApi(api);
        handleRequest(httpCallProxy,api,httpRequestCallback);
        return (R) ((MyHttpCall)httpCallProxy.getCall()).result;
    }

    @Override
    public <R> HttpCallProxy asyncRequest(final BaseApi api,final  @NonNull HttpRequestCallback<R> httpRequestCallback) {
        final HttpCallProxy httpCallProxy = new HttpCallProxy();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                handleRequest(httpCallProxy,api,httpRequestCallback);
            }
        });
        return httpCallProxy;
    }

    @Override
    public <R> HttpCallProxy sendRequest(final BaseApi api,final  @NonNull HttpRequestCallback<R> httpRequestCallback) {
        final HttpCallProxy httpCallProxy = new HttpCallProxy();
        api.getWorkScheduler().execute(new Runnable() {
            @Override
            public void run() {
                handleRequest(httpCallProxy,api,httpRequestCallback);
            }
        });
        return httpCallProxy;
    }

    private <R> void handleRequest(final HttpCallProxy httpCallProxy,BaseApi api,final @NonNull HttpRequestCallback<R> httpRequestCallback){
        MyHttpCall httpCall = new MyHttpCall();
        httpCallProxy.setCall(httpCall);
        if(httpCallProxy.getApi() == null){
            httpCallProxy.setApi(api);
        }
        OutputStream out = null;
        BufferedInputStream bis;
        HttpURLConnection httpconnection = null;
        try {
            QuttsSchedulers.AndroidMainThread.execute(new Runnable() {
                @Override
                public void run() {
                    httpRequestCallback.onStart();
                }
            });
            String baseUrl = api.getBaseUrl();
            Map<String,String> pathParams = api.getPathParams();
            if(pathParams != null && baseUrl.contains("{") && baseUrl.contains("}")){
                for(String key :pathParams.keySet()){
                    baseUrl = baseUrl.replace(String.format("{%s}",key),pathParams.get(key));
                }
            }
            StringBuilder httpUrl = new StringBuilder(baseUrl);
            if(!TextUtils.isEmpty(api.getApi())){
                if(api.getBaseUrl().endsWith("/")){
                    httpUrl.append(api.getApi());
                }else {
                    httpUrl.append('/');
                    httpUrl.append(api.getApi());
                }
            }
            if(!TextUtils.isEmpty(api.getQueryParams())){
//                String[] queryKeyValues = api.getQueryParams().split("&");
//                if(queryKeyValues != null){
//                    StringBuffer queryParams = new StringBuffer("?");
//                    for(int i = 0;i<queryKeyValues.length;i++){
//                        String keyValue = queryKeyValues[i];
//                        queryParams.append(URLEncoder.encode(keyValue,"UTF-8"));
//                        if(i != queryKeyValues.length-1){
//                            queryParams.append("&");
//                        }
//
//                    }
//
//                }
                httpUrl.append('?');
                httpUrl.append(api.getQueryParams());
            }

            URL url = new URL(httpUrl.toString());
            httpconnection = (HttpURLConnection) url.openConnection();
            httpCall.mHttpURLConnection = httpconnection;
            httpconnection.setRequestMethod(api.getHttpMethod());
            httpconnection.setDoInput(true);
            httpconnection.setDoOutput(true);
            if(SimpleApi.HttpMethod.POST.equalsIgnoreCase(api.getHttpMethod())){
                httpconnection.setUseCaches(false);
            }
            httpconnection.setReadTimeout(5000);
            httpconnection.setConnectTimeout(5000);
            httpconnection.setRequestProperty("Connection", "keep-alive");
            httpconnection.setRequestProperty("Accept-Charset", "UTF-8");
            httpconnection.setRequestProperty("Accept", "application/json");
            httpconnection.addRequestProperty("User-Agent", String.format(
                    "%s/%s (Linux; Android %s; %s Build/%s)", "Outts",
                    Qutts.Version, Build.VERSION.RELEASE, Build.MANUFACTURER,
                    Build.ID));
            if(httpRequestCallback.onInterceptRequest(httpCallProxy)){
                // 拦截网络请求
                return;
            }
            Map<String,String> headers = api.getHeaders();
            if(headers != null){
                for(String key:headers.keySet()){
                    httpconnection.setRequestProperty(key, headers.get(key));
                }
            }
            long dataLen = 0;
            HttpEntity httpEntity = api.getHttpEntity();
            if(httpEntity != null){
                dataLen = httpEntity.getLength();
                httpconnection.addRequestProperty("Content-Length",
                        String.valueOf(dataLen));
                if(!httpEntity.isMultipart() && httpEntity.fileHttpBodyList.size() == 0){
                    httpconnection.addRequestProperty("Content-Type", httpEntity.normalHttpBodyList.get(0).getMediaType());
                }
            }else {
                httpconnection.addRequestProperty("Content-Length",
                        String.valueOf(dataLen));
            }
            httpconnection.connect();
            out = httpconnection.getOutputStream();
            httpCall.out = out;
            String jsonParams = null;
            if(dataLen >0){
                if(httpEntity.isMultipart() && httpEntity.fileHttpBodyList.size()>0){
                    //https://www.cnblogs.com/h--d/p/5638092.html
                    Log.e(Qutts.TAG,"DefaultHttpEngine暂不支持上传文件!!!");
                }else {
                    HttpBody httpBody = httpEntity.normalHttpBodyList.get(0);
                    if(httpBody instanceof StringBody){
                        jsonParams = ((StringBody)httpBody).getContent();
                    }
                    byte[] buff = new byte[1024];
                    int offset = 0;
                    int uploadedCount = 0;
                    while ((offset = httpBody.readBuffer(offset,buff))!=-1){
                        uploadedCount += offset;
                        out.write(buff,0,offset);
                        offset = uploadedCount;
                        httpRequestCallback.onDataUploading(uploadedCount,dataLen);
                    }
                }
            }
            out.flush();
            int stateCode = httpconnection.getResponseCode();
            if (stateCode != 200) {
                if(Qutts.IsDebug){
                    Log.e(Qutts.TAG,
                            "handleRequest() Error at called by interface " + httpUrl + ": jsonParams="
                                    + jsonParams + ",httpStateCode=" + stateCode
                                    + ",reslut= "
                                    + httpconnection.getResponseMessage());
                }
                httpRequestCallback.onError(httpCallProxy,new RuntimeException(String.format("httpStateCode:%d, message:%s",stateCode,httpconnection.getResponseMessage())));
                return;
            }
            Map<String, List<String>> responseHeaders = httpconnection.getHeaderFields();
            httpRequestCallback.onReceivedResponseHeaders(httpCallProxy,responseHeaders);
            bis = new BufferedInputStream(httpconnection.getInputStream());
            httpCall.in = bis;
            if(stateCode == 200){
                //获取内容长度
                int contentLength = httpconnection.getContentLength();
                int downloadedCount = 0;
                byte[] buff = new byte[2048];
                int len;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((len =bis.read(buff))!=-1) {
                    downloadedCount += len;
                    bos.write(buff,0,len);
                    httpRequestCallback.onDownloading(downloadedCount,contentLength);
                }
                byte[] rawData = bos.toByteArray();
                final R data = httpRequestCallback.convert(httpCallProxy,rawData);
                httpCall.result = data;
                if(Qutts.IsDebug){
                    Log.w(Qutts.TAG,"handleRequest() api: "+httpUrl+",jsonParams: "+jsonParams+", result: "+new String(rawData));
                }
                if(!httpCallProxy.isConvertError()){
                    api.getNotifyScheduler().execute(new Runnable() {
                        @Override
                        public void run() {
                            httpRequestCallback.onSuccess(httpCallProxy,data);
                        }
                    });
                }


            }
        }catch (Exception e){
            e.printStackTrace();
            httpRequestCallback.onError(httpCallProxy,e);
        }finally {
            httpCall.cancel();
        }
    }

    public static class MyHttpCall implements HttpCall{
        private HttpURLConnection mHttpURLConnection;
        private BufferedInputStream in;
        private OutputStream out;
        private boolean isCancel;
        private Object result;
        @Override
        public boolean isCanceled() {
            return isCancel;
        }
        
         public HttpURLConnection getHttpURLConnection(){
            return mHttpURLConnection;
        }

        @Override
        public void cancel() {
            isCancel = true;
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(mHttpURLConnection != null){
                try {
                    mHttpURLConnection.disconnect();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

}
