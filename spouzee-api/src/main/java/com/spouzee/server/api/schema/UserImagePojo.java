package com.spouzee.server.api.schema;

/**
 * Created by Sagar on 3/6/2016.
 */
public class UserImagePojo {

    private long id;
    private String description;

    public UserImagePojo(long id, String description) {
        this.id = id;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
