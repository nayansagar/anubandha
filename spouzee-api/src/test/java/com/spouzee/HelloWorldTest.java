package com.spouzee;

import com.spouzee.server.api.resources.HelloWorld;
import org.hamcrest.Matchers;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.jayway.restassured.RestAssured.get;
import static org.hamcrest.Matchers.equalTo;

public class HelloWorldTest {

    private TJWSEmbeddedJaxrsServer server;

    @BeforeTest
    public void setup() {
        server = new TJWSEmbeddedJaxrsServer();
        server.setPort(8080);
        server.start();
        server.getDeployment().getRegistry().addPerRequestResource(HelloWorld.class);

    }

    @AfterTest
    public void tearDown() {
        server.stop();
    }


    //@Test
    public void helloWorld() throws Exception {
        get("/hello").
                then().
                assertThat().statusCode(200).and().body(equalTo("Hello World"));
    }

    //@Test
    public void echoAPI() throws Exception {
        UUID uuid = UUID.randomUUID();
        get("/hello/echo/" + uuid.toString()).
                then().
                assertThat().statusCode(200).body(equalTo(uuid.toString()));
    }
}