package org.androware.flow.base;

import org.androware.androbeans.utils.ConstructorSpec;

import java.util.List;


/**
 * Created by jkirkley on 5/7/16.
 */
public class AdapterViewSpec {

    public List<String> beanIds;  // TODO, should be an array of ids at least, but probably more framework needed here
    public String viewId;
    public String itemLayoutId;
    public boolean useDefault;

    public ConstructorSpec adapterConstructorSpec;

    public List<Object> items;

    public ConstructorSpec itemGeneratorSpec;

    private ItemGenerator itemGenerator;

    public AdapterViewSpec() {
    }

    public Object getDefaultItemSpec(){
        return getItemSpec(0);
    }

    public Object getItemSpec(int position){
        if(useDefault) {
            return items.get(0);
        }
        return items.get(position);
    }

    public void __init__() {
        if(itemGeneratorSpec != null){
            itemGenerator = (ItemGenerator) itemGeneratorSpec.build();
        }
    }

    public List getItems(Object spec) {
        if(items == null && itemGenerator != null) {
            items = itemGenerator.getItems(spec);
        }

        return items;
    }

}

