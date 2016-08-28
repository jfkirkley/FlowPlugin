package org.androware.flow.base;

import com.intellij.vcs.log.Hash;
import org.androware.flow.builder.TwoWayMapperForm;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jkirkley on 8/16/16.
 */

public class TwoWayMapperBase {
    public HashMap<String, String> componentId2BeanFieldMap;

    public TwoWayMapperBase() {
    }

    public TwoWayMapperBase(HashMap map) {
        this.componentId2BeanFieldMap = map;
    }
}
