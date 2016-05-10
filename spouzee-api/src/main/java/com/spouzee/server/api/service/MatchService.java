package com.spouzee.server.api.service;

import com.spouzee.server.api.schema.DisussionMessage;
import com.spouzee.server.api.schema.DisussionUpload;
import com.spouzee.server.api.schema.Scenario;
import com.spouzee.server.db.dao.SpzUserDao;
import com.spouzee.server.db.entities.MatchEntity;
import com.spouzee.server.db.entities.ScenarioDiscussion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 4/27/2016.
 */
@Service
public class MatchService {

    private SpzUserDao spzUserDao;

    @Autowired
    public MatchService(SpzUserDao spzUserDao) {
        this.spzUserDao = spzUserDao;
    }

    public List<DisussionMessage> getDiscussion(long matchId, long scenarioId) {
        List<ScenarioDiscussion> scenarioDiscussionList = spzUserDao.getScenarioDiscussion(matchId, scenarioId);
        if(scenarioDiscussionList == null || scenarioDiscussionList.isEmpty()){
            return new ArrayList<>();
        }
        List<DisussionMessage> disussionMessages = new ArrayList<>();
        for(ScenarioDiscussion sd : scenarioDiscussionList){
            DisussionMessage disussionMessage = new DisussionMessage(sd.getUser().getId(), sd.getMessage(), sd.getCreatedAt());
            disussionMessages.add(disussionMessage);
        }
        return disussionMessages;
    }

    public void addToDiscussion(long matchId, DisussionUpload du) {
        spzUserDao.addMessageToScenario(du.getUserId(), du.getScenarioId(), matchId, du.isComplete(), du.getMessage());
        if(du.isComplete()){
            spzUserDao.markDiscussionCompleteForUser(du.getMatchId(), du.getScenarioId(), du.getUserId());
        }
    }

    public List<Scenario> getScenarios(long matchId, int pageSize) {
        List<com.spouzee.server.db.entities.Scenario> scenarioEntities =  spzUserDao.getScenarioForMatch(matchId, pageSize);
        List<Scenario> scenarios = new ArrayList<>();
        for(com.spouzee.server.db.entities.Scenario scenario : scenarioEntities){
            Scenario sc = new Scenario(scenario.getId(), scenario.getScenarioTitle(), scenario.getScenarioDescription());
            scenarios.add(sc);
        }
        List<ScenarioDiscussion> scenarioDiscussionList = spzUserDao.getScenarioDiscussion(matchId, scenarios.get(0).getId());
        if(scenarioDiscussionList == null || scenarioDiscussionList.isEmpty()){
            MatchEntity match = spzUserDao.getMatch(matchId);
            spzUserDao.addMessageToScenario(match.getUserId1(), scenarios.get(0).getId(), matchId, false, "...");
            spzUserDao.addMessageToScenario(match.getUserId2(), scenarios.get(0).getId(), matchId, false, "...");
        }
        return scenarios;
    }
}
