package com.spouzee.server.api.data;

/**
 * Created by Sagar on 8/30/2015.
 */
public class CreateUserResponse {

    private long userId;

    private boolean userAlreadyExists;

    private boolean profileDataSubmitted;

    public CreateUserResponse(long userId, boolean userAlreadyExists, boolean profileDataSubmitted) {
        this.userId = userId;
        this.userAlreadyExists = userAlreadyExists;
        this.profileDataSubmitted = profileDataSubmitted;
    }

    public long getUserId() {
        return userId;
    }

    public boolean isUserAlreadyExists() {
        return userAlreadyExists;
    }

    public boolean isProfileDataSubmitted() {
        return profileDataSubmitted;
    }
}
