package com.offlineprogrammer.braintrainer.game;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.database.Exclude;


public class HighScoreGame {



    private Date datePlayed;
    private Integer score;
    private Integer numberOfCorrectAnswers;
    private Integer numberOfWrongAnswers;



    public HighScoreGame(Date datePlayed, Integer score, Integer numberOfCorrectAnswers, Integer numberOfWrongAnswers) {


        this.datePlayed = datePlayed;
        this.score = score;
        this.numberOfCorrectAnswers = numberOfCorrectAnswers;
        this.numberOfWrongAnswers = numberOfWrongAnswers;
    }

    public HighScoreGame() {

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("score", this.score);
        result.put("numberOfCorrectAnswers", this.numberOfCorrectAnswers);
        result.put("datePlayed", this.datePlayed);

        result.put("numberOfWrongAnswers", this.numberOfWrongAnswers);
        return result;
    }





    public Date getDatePlayed() {
        return datePlayed;
    }

    public void setDatePlayed(Date datePlayed) {
        this.datePlayed = datePlayed;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getNumberOfCorrectAnswers() {
        return numberOfCorrectAnswers;
    }

    public void setNumberOfCorrectAnswers(Integer numberOfCorrectAnswers) {
        this.numberOfCorrectAnswers = numberOfCorrectAnswers;
    }

    public Integer getNumberOfWrongAnswers() {
        return numberOfWrongAnswers;
    }

    public void setNumberOfWrongAnswers(Integer numberOfWrongAnswers) {
        this.numberOfWrongAnswers = numberOfWrongAnswers;
    }

}
