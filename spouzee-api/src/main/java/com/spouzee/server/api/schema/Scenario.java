package com.spouzee.server.api.schema;

/**
 * Created by Sagar on 8/20/2015.
 */
public class Scenario {

    long id;

    String title;

    String descripton;

    public Scenario(long id, String title, String descripton) {
        this.id = id;
        this.title = title;
        this.descripton = descripton;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescripton() {
        return descripton;
    }
}
