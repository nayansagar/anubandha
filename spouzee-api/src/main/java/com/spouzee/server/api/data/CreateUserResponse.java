package com.spouzee.server.api.data;

/**
 * Created by Sagar on 8/30/2015.
 */
public class CreateUserResponse {

    private long userId;

    private boolean userAlreadyExists;

    public CreateUserResponse(long userId, boolean userAlreadyExists) {
        this.userId = userId;
        this.userAlreadyExists = userAlreadyExists;
    }

    public long getUserId() {
        return userId;
    }

    public boolean isUserAlreadyExists() {
        return userAlreadyExists;
    }
}
