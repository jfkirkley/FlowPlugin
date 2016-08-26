package org.androware.flow.base;

import org.androware.androbeans.utils.ConstructorSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jkirkley on 8/16/16.
 */

public class StepBase {
    public String layout;
    public String processor;
    public String parentContainer;
    public String transitionClassName;


    public TwoWayMapperBase twoWayMapper;
    public List<ObjectLoaderSpecBase> objectLoaderSpecs;

    public ObjectSaverSpecBase objectSaverSpec;
    public String targetFlow;
    public UI ui;
    public HashMap<String, NavBase> navMap;
    public ConstructorSpec viewCustomizerSpec;
    public HashMap<String, String> meta;
    public HashMap<String, String> data;

    public void __get_type_overrides__(Map map) {
        map.put(TwoWayMapperBase.class, "org.androware.flow.binding.TwoWayMapper");
        map.put(ObjectLoaderSpecBase.class, "org.androware.flow.binding.ObjectLoaderSpec");
        map.put(ObjectSaverSpecBase.class, "org.androware.flow.binding.ObjectSaverSpec");
        map.put(NavBase.class, "org.androware.flow.Nav");
    }


}
