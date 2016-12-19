package org.androware.flow.plugin;

import java.util.List;
import java.util.Map;

/**
 * Created by jkirkley on 12/16/16.
 */
public class TestBean {

    public static class SubBean {
        public String string;
        public Integer integer;
        public Map<Float, SubBean2> subSubBeanMap;
    }

    public static class SubBean2 {
        public String string;
        public Integer integer;
        public Float fff;
    }

    public List<SubBean> subBeanList;
    public Map<Float, SubBean> subBeanMap;

    public SubBean subBean;
}
