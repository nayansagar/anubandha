package com.spouzee.server.api.resources;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;


@Path("/hello")
public class HelloWorld {

    @GET
    @Produces("text/plain")
    public String hello(){
        return "Hello World";
    }

    @GET
    @Path("/echo/{message}")
    @Produces("text/plain")
    public String echo(@PathParam("message") String message){
        return message;
    }

}