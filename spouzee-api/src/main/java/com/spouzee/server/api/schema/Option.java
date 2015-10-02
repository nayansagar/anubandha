package com.spouzee.server.api.schema;

/**
 * Created by Sagar on 8/20/2015.
 */
public class Option {

    long id;

    String description;

    String shortDescription;

    private boolean selected=false;

    public Option(long id, String description, String shortDescription) {
        this.id = id;
        this.description = description;
        this.shortDescription = shortDescription;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
