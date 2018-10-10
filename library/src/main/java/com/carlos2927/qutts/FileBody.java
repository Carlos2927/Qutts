package com.carlos2927.qutts;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * https://blog.csdn.net/jdsjlzx/article/details/52246114
 */
public class FileBody implements HttpBody<File> {
    private File file;
    private String mediaType;
    RandomAccessFile randomAccessFile;
    private String name;


    public FileBody(File file, String mediaType){
        this.file = file;
        this.mediaType = mediaType;
    }

    @Override
    public File getContent() {
        return file;
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
        return file == null?0:file.length();
    }

    @Override
    public int readBuffer(int offset, byte[] buffer) {
        try {
            if(randomAccessFile == null){
                randomAccessFile = new RandomAccessFile(file,"r");
            }
            return randomAccessFile.read(buffer,offset,buffer.length);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] readBytes() {
        return null;
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
