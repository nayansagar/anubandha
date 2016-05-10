package com.spouzee.server.api.matcher;

import com.spouzee.server.api.exception.UserNotFoundException;
import com.spouzee.server.api.schema.Match;

import java.util.List;

/**
 * Created by Sagar on 10/3/2015.
 */
public interface IMatcher {
    public List<Match> getMatchingUsers(long userId) throws UserNotFoundException;
}
