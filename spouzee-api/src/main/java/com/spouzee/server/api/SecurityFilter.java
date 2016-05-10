package com.spouzee.server.api;

import com.spouzee.server.db.dao.SpzUserDao;
import com.spouzee.server.db.entities.MatchEntity;
import com.spouzee.server.db.entities.User;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@Component
public class SecurityFilter implements ContainerRequestFilter {

    @Autowired
    private SpzUserDao spzUserDao;

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.DEFAULT)
    public void filter(ContainerRequestContext crc) throws IOException {

        String path = crc.getUriInfo().getPath();

        String sessionId = crc.getHeaders().getFirst("sid");

        if (isInvalidRequest(crc, path)) return;

        if (isAuthRequest(crc, path)) return;

        validateSession(crc, sessionId);

        if (isImageRequest(crc, path, sessionId)) return;
    }

    private void validateSession(ContainerRequestContext crc, String sessionId) {
        Long requestingUserId = getLongParameter("ru", crc.getUriInfo().getQueryParameters());
        if(!spzUserDao.isSessionValid(sessionId, requestingUserId)){
            crc.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }
    }

    private boolean isAuthRequest(ContainerRequestContext crc, String path) {
        if ("/v1/users".equals(path) && "POST".equals(crc.getMethod())) {
            return true;
        }
        return false;
    }

    private boolean isInvalidRequest(ContainerRequestContext crc, String path) {
        if(path.contains("%7B") || path.contains("%7D")){
            crc.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
            return true;
        }
        return false;
    }

    private boolean isImageRequest(ContainerRequestContext crc, String path, String sessionId) {
        if ( path.startsWith("/v1/users/") && "GET".equalsIgnoreCase(crc.getMethod()) &&
                ( path.contains("/picture") || path.contains("/images") ) ) {
            System.out.println("PATH : " + path + ", SID : " + sessionId);

            String size = getStringParameter("size", crc.getUriInfo().getQueryParameters());

            Long requestedUserId = getLongParameter("user_id", crc.getUriInfo().getPathParameters());
            Long requestingUserId = getLongParameter("ru", crc.getUriInfo().getQueryParameters());

            if(requestingUserId == null || requestedUserId == null){
                crc.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
                return true;
            }

            if(requestedUserId.equals(requestingUserId)){
                return true;
            }

            MatchEntity me = spzUserDao.getMatch(requestingUserId, requestedUserId);
            if(me != null){
                if(( (me.isUser1Interested() && me.isUser2Responded()) || (me.isUser2Interested() && me.isUser1Responded()) )){
                    return true;
                }else{
                    crc.abortWith(Response.temporaryRedirect(getPlaceHolderLocation(size)).build());
                    return true;
                }
            }else{
                crc.abortWith(Response.status(Response.Status.FORBIDDEN).build());
                return true;
            }

        }
        return false;
    }

    private boolean isMatchStatusRequest(ContainerRequestContext crc, String path, String sessionId) {
        if ( path.startsWith("/v1/users/") && "GET".equalsIgnoreCase(crc.getMethod()) &&
                ( path.contains("/interest") || path.contains("/interestResponse")
                || path.contains("/requestContact") || path.contains("/revealContact")
                || path.contains("/contact")) ) {
            System.out.println("PATH : "+ path +", SID : "+sessionId);

            Long requestedUserId = getLongParameter("otherUserId", crc.getUriInfo().getQueryParameters());
            Long requestingUserId = getLongParameter("ru", crc.getUriInfo().getQueryParameters());

            if(requestingUserId == null || requestedUserId == null || requestedUserId.equals(requestingUserId)){
                crc.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
                return true;
            }

            MatchEntity me = spzUserDao.getMatch(requestingUserId, requestedUserId);
            if(me == null) {
                crc.abortWith(Response.status(Response.Status.FORBIDDEN).build());
                return true;
            }
        }
        return false;
    }

    private URI getPlaceHolderLocation(String size) {
        try {
            if(size != null && (size.equals("square") || size.equals("small") )){
                return new URI("/static/images/profile-placeholder2.jpg");
            }else{
                return new URI("/static/images/profile-placeholder.jpg");
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getStringParameter(String userIdKey, MultivaluedMap<String, String> pathParameters) {
        for(String key : pathParameters.keySet()){
            if(key.equals(userIdKey)){
                return pathParameters.getFirst(key);
            }
        }
        return null;
    }

    private Long getLongParameter(String userIdKey, MultivaluedMap<String, String> pathParameters) {
        for(String key : pathParameters.keySet()){
            if(key.equals(userIdKey)){
                return Long.parseLong(pathParameters.getFirst(key));
            }
        }
        return null;
    }
}