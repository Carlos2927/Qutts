package com.carlos2927.qutts;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpEntity {
     List<HttpBody> normalHttpBodyList = new ArrayList<>();
     List<FileBody> fileHttpBodyList = new ArrayList<>();
     private long length;

    private HttpEntity(){}

    public static HttpEntity create(HttpBody ... httpBodies){
        HttpEntity httpEntity = new HttpEntity();
        if(httpBodies != null){
            for(HttpBody httpBody:httpBodies){
                httpEntity.addHttpBody(httpBody);
            }
        }
        return httpEntity;
    }
     
    public int getHttpBodyCount(){
         return normalHttpBodyList.size();
    }
     
    public int getFileBodyCount(){
         return fileHttpBodyList.size();
    }
    
    public HttpBody getNormalHttpBody(int index){
         return index>=0 && index< normalHttpBodyList.size()?normalHttpBodyList.get(index):null;
    }
    public FileBody getFileBody(int index){
         return index>=0 && index< fileHttpBodyList.size()?fileHttpBodyList.get(index):null;
    }

    public long getLength(){
       return length;
    }

    public HttpEntity addHttpBody(HttpBody httpBody){
        if(httpBody != null){
            if(httpBody instanceof FileBody){
                fileHttpBodyList.add((FileBody)httpBody);
            }else {
                normalHttpBodyList.add(httpBody);
            }
            length += httpBody.length();
        }
        return this;
    }

    public Map<String,String> getMultipartNoFileParams(){
        if(isMultipart()){
            HashMap<String,String> params = new HashMap<>();
            for(HttpBody httpBody:normalHttpBodyList){
                if(httpBody instanceof StringBody){
                    StringBody stringBody = (StringBody) httpBody;
                    if(stringBody.isAsFileUploadParams()){
                        try {
                            JSONObject jsonObject = new JSONObject(stringBody.getContent());
                            Iterator<String> keys = jsonObject.keys();
                            while (keys.hasNext()){
                                String key = keys.next();
                                params.put(key,jsonObject.get(key).toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return params;
        }
        return null;
    }

    public boolean isMultipart(){
        return normalHttpBodyList.size()>1 && fileHttpBodyList.size()>1;
    }
}
