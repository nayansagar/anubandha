package com.spouzee.server.api.exception;

/**
 * Created by Sagar on 8/9/2015.
 */
public class EmailOrPasswordInvalidException extends Throwable {
    public EmailOrPasswordInvalidException(String message) {
        super(message);
    }
}
