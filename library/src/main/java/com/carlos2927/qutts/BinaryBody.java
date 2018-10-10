package com.carlos2927.qutts;

public class BinaryBody implements HttpBody<byte[]> {
    private String name;
    private String mediaType;
    private byte[] content;

    public BinaryBody(String name, byte[] content, String mediaType){
        this.name = name;
        this.content = content;
        this.mediaType = mediaType;
    }
    @Override
    public byte[] getContent() {
        return content;
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
        return content == null?0:content.length;
    }

    @Override
    public int readBuffer(int offset, byte[] buffer) {
        if(offset <0){
            throw new RuntimeException("Invalid offset:"+offset+",contentLength:"+content.length);
        }
        if(offset>content.length-1){
            return -1;
        }
        int remainder = content.length - offset;
        if(remainder <= buffer.length){
            System.arraycopy(content,offset,buffer,0,remainder);
        }else {
            remainder = buffer.length;
            System.arraycopy(content,offset,buffer,0,remainder);
        }
        return remainder;
    }

    @Override
    public byte[] readBytes() {
        return content;
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
