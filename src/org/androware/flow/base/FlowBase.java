package org.androware.flow.base;

import org.androware.androbeans.utils.ConstructorSpec;
import org.intellij.lang.annotations.Flow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jkirkley on 8/16/16.
 */

public class FlowBase {
    public HashMap<String, StepBase> steps;
    public String layout;
    public String processor;
    public List<ObjectLoaderSpecBase> objectLoaderSpecs;
    public ConstructorSpec stepGeneratorSpec;
    public NavBase startNav;    // navigates to the first step
    public String fragmentContainer;

    public static FlowBase currFlowBase = null;
    public FlowBase() {
        currFlowBase = this;
    }

    public void __get_type_overrides__(Map map) {
        map.put(StepBase.class, "org.androware.flow.Step");
        map.put(NavBase.class, "org.androware.flow.Nav");
        map.put(NavBase.class, "org.androware.flow.Nav");
        map.put(ObjectLoaderSpecBase.class, "org.androware.flow.binding.ObjectLoaderSpec");
    }

    public static Set<String> ignoreTheseLoaderClasses;  // TODO,  to avoid things like CachedObjectLoader

    public void addLoadedObjectsToRegistry(Map<String, String> registry) {
        for(ObjectLoaderSpecBase objectLoaderSpecBase: objectLoaderSpecs) {
            if(objectLoaderSpecBase.objectClassName != null && objectLoaderSpecBase.objectClassName.length()>0) {
                registry.put(objectLoaderSpecBase.toString(), objectLoaderSpecBase.objectClassName);
            }
        }
    }

    public Map buildRegistry(StepBase stepBase) {
        Map<String, String> registry = new HashMap<>();
        addLoadedObjectsToRegistry(registry);
        stepBase.addLoadedObjectsToRegistry(registry);
        return registry;
    }

    public Map buildRegistry() {
        Map<String, String> registry = new HashMap<>();
        addLoadedObjectsToRegistry(registry);
        if(steps != null){
            for(String k : steps.keySet()) {
                StepBase stepBase = steps.get(k);
                stepBase.addLoadedObjectsToRegistry(registry);
            }
        }

        return registry;
    }
}
