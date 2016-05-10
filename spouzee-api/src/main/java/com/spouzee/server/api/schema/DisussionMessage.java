package com.spouzee.server.api.schema;

import org.apache.james.mime4j.field.datetime.DateTime;

import java.util.Date;

/**
 * Created by Sagar on 4/27/2016.
 */
public class DisussionMessage {

    private long userId;

    private String message;

    private Date writtenAt;

    public DisussionMessage(long userId, String message, Date writtenAt) {
        this.userId = userId;
        this.message = message;
        this.writtenAt = writtenAt;
    }

    public long getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public Date getWrittenAt() {
        return writtenAt;
    }
}
