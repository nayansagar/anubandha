package com.spouzee.server.db.entities;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Sagar on 8/20/2015.
 */
@Entity
@Table(name = "scenario")
public class Scenario {

    @GeneratedValue
    @Column(name = "id")
    @Id
    private  long id;

    @Column(name = "scenario_title")
    private String scenarioTitle;

    @Column(name = "scenario_description")
    private String scenarioDescription;

    @OneToMany(mappedBy = "scenario",cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    private List<Question> questions;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getScenarioTitle() {
        return scenarioTitle;
    }

    public void setScenarioTitle(String scenarioTitle) {
        this.scenarioTitle = scenarioTitle;
    }

    public String getScenarioDescription() {
        return scenarioDescription;
    }

    public void setScenarioDescription(String scenarioDescription) {
        this.scenarioDescription = scenarioDescription;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
