package com.spouzee.server.api.schema;

/**
 * Created by Sagar on 3/6/2016.
 */
public class ImagePojo {

    private String contentType;

    private byte[] content;

    public ImagePojo(String contentType, byte[] content) {
        this.contentType = contentType;
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }
}
