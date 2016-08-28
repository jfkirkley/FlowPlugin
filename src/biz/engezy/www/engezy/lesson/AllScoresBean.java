package biz.engezy.www.engezy.lesson;



import java.util.HashMap;
import java.util.Map;


/**
 * Created by jkirkley on 8/9/16.
 */

public class AllScoresBean {

    Map<String, ScoreBean>  stepName2ScoreBeanMap;


    public AllScoresBean() {
        stepName2ScoreBeanMap = new HashMap<>();
    }

    public void addScoreBean(String id, ScoreBean scoreBean) {
        stepName2ScoreBeanMap.put(id, scoreBean);
    }


    private int getTotalForPrefix(String prefix) {
        int total = 0;
        int count = 0;

        for(String scoreId: stepName2ScoreBeanMap.keySet()) {
            if(scoreId.startsWith(prefix)) {
                count++;
                total += stepName2ScoreBeanMap.get(scoreId).getTotalScoreAsInt();
            }
        }
        return total > 0? total/count: 0;
    }


    public String totalScoreForStep() {
        return null;
    }

    public String setTotalScoreForStep(Object ignoreMe) {
        return totalScoreForStep();
    }


    public String getCheckImage(String prefix) {
        if( getTotalForPrefix(prefix) > 0) {
            return "green_check_box";
        }
        return "gray_check_box";
    }

    public String getCheckImageForRoot(){
        return null;
    }

    public String getCheckImageForStep(){
        return null;
    }
}
