package biz.engezy.www.engezy.lesson;



import java.util.HashMap;
import java.util.Map;



/**
 * Created by jkirkley on 7/22/16.
 */
public class ScoreBean {

    public static final String TAG = "scores";

    public Map<String, Integer> scores;

    private String currText;
    private String lastScore = "0";

    public ScoreBean() {

    }

    public void l(String s) {
        System.out.println(TAG + "; " + s);
    }


    public void addScore(String text, Integer score) {
        scores.put(text, score);
    }

    public Integer getScore(String text) {
        return scores.get(text);
    }

    public String getCurrText() {
        return currText;
    }

    public void setCurrText(String currText) {
        l("setText: " + currText);
        this.currText = currText;
    }

    // set method
    public void score(String score) {
        if(scores == null){
            scores = new HashMap<>();
        }
        l("score: " + currText + " --> " + score);
        if(this.currText != null) {
            if(score.endsWith("%"))
            {
                score = score.substring(0, score.length()-1);
            }
            scores.put(currText, Integer.parseInt(score));
            lastScore = score.toString();
        }
    }

    // get method
    public String score() {

        if(this.currText != null && scores != null && scores.containsKey(currText)) {
            return scores.get(currText).toString();
        }
        return lastScore + "%";
    }

    public Map<String, Integer> getScores() {
        return scores;
    }

    public int getTotalScoreAsInt() {
        if( scores != null ) {
            int total = 0;
            for (String t : scores.keySet()) {
                int s = scores.get(t);
                total += s;
            }
            int finalScore = total / scores.size();
            return finalScore;
        }
        return 0;
    }

    public String getTotalScore() {

        return getTotalScoreAsInt() + "%";
    }

    public String setTotalScore(Object ignoreMe) {
        return getTotalScore();
    }

    public String getStatus() {
        if(scores == null) {
            return "No scores yet.   Take the test to get a score!";
        }
        return "";
    }
}
