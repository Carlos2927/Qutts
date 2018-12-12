package com.carlos2927.qutts;

public interface HttpBody<T> {
    T getContent();
    String getMediaType();
    HttpBody setMediaType(String mediaType);
    long length();
    int readBuffer(int offset, byte[] buffer);
    int readBuffer(long offset, byte[] buffer);
    byte[] readBytes();

    /**
     *  Content-Disposition: form-data; name="text1"
     *  foo
     * @return the value of Content-Disposition's name ( e.g text1)
     */
    String getContentKey();
    <R extends HttpBody> R setContentKey(String name);
}
