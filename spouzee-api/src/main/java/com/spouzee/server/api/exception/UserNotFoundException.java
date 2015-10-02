package com.spouzee.server.api.exception;

/**
 * Created by Sagar on 8/3/2015.
 */
public class UserNotFoundException extends Throwable {
    public UserNotFoundException(String message) {
        super(message);
    }
}
