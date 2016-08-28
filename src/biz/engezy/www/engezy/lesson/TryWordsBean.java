package biz.engezy.www.engezy.lesson;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jkirkley on 8/3/16.
 */

public class TryWordsBean {
    public final static String IS_TRY_WORDS = "isTryWords";

    public List<String>  words;

    //private String currWord = null;

    public TryWordsBean() {
    }

    public void __init__() {
        if(words == null) {
            words = new ArrayList<>();
        }
    }

    // setter
    public void words(CharSequence newWords) {
        if( words == null) {
            words = new ArrayList<>();
        }
        words.add(newWords.toString());

    }

    public String words() {
        return "";
    }

    public List<String> getWords() {
        if( words == null) {
            words = new ArrayList<>();
        }
        return words;
    }

    public Map makePropMap(int index) {
        Map propmap = new HashMap();
        propmap.put("studyIndex", index);
        propmap.put(IS_TRY_WORDS, true);

        return propmap;
    }

    public Map makeListMap(String text, int index) {
        Map map = new HashMap();
        map.put("labelFieldId", "labelField");
        map.put("label", text);
        map.put("target", "startLesson");


        map.put("props", makePropMap(index));

        return map;
    }


    public void update(Object updateObject) {

    }

    public static boolean isTryWords(Map params) {
        return params.containsKey(TryWordsBean.IS_TRY_WORDS) && (boolean)params.get(TryWordsBean.IS_TRY_WORDS);
    }
}
