package com.spouzee.server.api.exception;

/**
 * Created by Sagar on 8/9/2015.
 */
public class EmailAlreadyExistsException extends Throwable {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
