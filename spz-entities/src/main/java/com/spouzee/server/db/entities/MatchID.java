package com.spouzee.server.db.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by Sagar on 3/12/2016.
 */
@Embeddable
public class MatchID implements Serializable {

    @Column(name = "userid1")
    private long userId1;

    @Column(name = "userid2")
    private long userId2;

    public MatchID(){}

    public MatchID(long userId1, long userId2) {
        this.userId1 = userId1;
        this.userId2 = userId2;
    }

    public long getUserId1() {
        return userId1;
    }

    public void setUserId1(long userId1) {
        this.userId1 = userId1;
    }

    public long getUserId2() {
        return userId2;
    }

    public void setUserId2(long userId2) {
        this.userId2 = userId2;
    }
}
