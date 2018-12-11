package com.carlos2927.qutts;

import java.util.ArrayList;
import java.util.List;

public class HttpEntity {
     List<HttpBody> normalHttpBodyList = new ArrayList<>();
     List<FileBody> fileHttpBodyList = new ArrayList<>();
     private long lenght;

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

    public long getLenght(){
       return lenght;
    }

    public HttpEntity addHttpBody(HttpBody httpBody){
        if(httpBody != null){
            if(httpBody instanceof FileBody){
                fileHttpBodyList.add((FileBody)httpBody);
            }else {
                normalHttpBodyList.add(httpBody);
            }
            lenght += httpBody.length();
        }
        return this;
    }

    public boolean isMultipart(){
        return normalHttpBodyList.size() +  fileHttpBodyList.size()>1;
    }
}
