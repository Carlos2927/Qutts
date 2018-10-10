package com.carlos2927.qutts;

import java.nio.charset.Charset;

public class StringBody implements HttpBody<String> {
    private Charset charset = null;
    private String content;
    private byte[] data;
    private String mediaType;
    private String name;

    public static StringBody createJsonStringBody(String content){
        return new StringBody(content,"application/json; charset=UTF-8");
    }
    public StringBody(String content, String mediaType){
        this(content,"UTF-8",mediaType);
    }
    public StringBody(String content, String charsetName, String mediaType){
        this.content = content;
        charset = Charset.forName(charsetName);
        setMediaType(mediaType);
    }

    @Override
    public String getContent() {
        return content;
    }

    public Charset getCharset(){
        return charset;
    }

    @Override
    public String getMediaType() {
        return mediaType;
    }

    @Override
    public HttpBody setMediaType(String mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public long length() {
        if(data == null){
            readBytes();
        }
        return data == null?0:data.length;
    }

    @Override
    public int readBuffer(int offset, byte[] buffer) {
        if(data == null){
            readBytes();
        }
        if(offset <0){
            throw new RuntimeException("Invalid offset:"+offset+",contentLength:"+data.length);
        }
        if(offset>data.length-1){
            return -1;
        }
        int remainder = data.length - offset;
        if(remainder <= buffer.length){
            System.arraycopy(data,offset,buffer,0,remainder);
        }else {
            remainder = buffer.length;
            System.arraycopy(data,offset,buffer,0,remainder);
        }
        return remainder;
    }

    @Override
    public byte[] readBytes() {
        if(data == null && content!=null){
            data = content.getBytes(charset);
        }
        return data;
    }

    @Override
    public String getContentKey() {
        return name;
    }


    @Override
    public <R extends HttpBody> R setContentKey(String name) {
        this.name = name;
        return (R)this;
    }

}
