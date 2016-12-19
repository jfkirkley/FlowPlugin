package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.androware.androbeans.utils.ConstructorSpec;
import org.androware.androbeans.utils.ReflectionUtils;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jkirkley on 8/24/16.
 */
public class ConstructorSpecForm implements CRUDForm<ConstructorSpec> {

    private ClassChooserWidget targetClassChooser;
    private JPanel rootPanel;
    private SimpleTypeListForm paramClassSimpleTypeListForm;
    private SimpleTypeListForm paramObjectSimpleTypeListForm;
    private AnyObjectForm paramAnyObjectForm;
    private ResourcePickerForm resourcePickerForm;
    private ConstructorSpec constructorSpec;

    List paramObjectList = new ArrayList<>();

    @Override
    public void populate(ConstructorSpec object) {
    }

    @Override
    public void done() {
        getTarget();
    }

    @Override
    public void handleChildValue(Object childValue) {

    }

    @Override
    public void init(Project project, ToolWindow toolWindow, ConstructorSpec target, FormAssembler formAssembler, CRUDForm parentForm) {

    }

    @Override
    public void init(Project project, ToolWindow toolWindow, ConstructorSpec target, FormAssembler formAssembler) {
    }

/*
    public class ParamObjectFormAssembler implements FormAssembler<SimpleTypeListForm> {

        @Override
        public void assemble(Project project, ToolWindow toolWindow, SimpleTypeListForm form) {
            form.getAddButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    Object v = paramAnyObjectForm.getTarget();
                    if(v != null) {
                        paramAnyObjectForm.clear();
                        form.getTypeListComboBox().addItem(v);
                    } else {
                        CompFactory.FieldWrap fieldWrap = (CompFactory.FieldWrap)resourcePickerForm.getTarget();
                        if(fieldWrap != null) {
                            v = fieldWrap.toString();
                            form.getTypeListComboBox().addItem(v);
                        }
                    }
                    if( v != null) {

                        if(constructorSpec.paramObjects == null) {
                            constructorSpec.paramObjects = new Object[1];
                        } else {
                            constructorSpec.paramObjects = Arrays.copyOf(constructorSpec.paramObjects, constructorSpec.paramObjects.length+1 );
                            constructorSpec.paramObjects[constructorSpec.paramObjects.length-1] = v;
                        }
                    }
                }
            });

        }
    }
*/

    @Override
    public void init(Project project, ToolWindow toolWindow, ConstructorSpec target) {
        constructorSpec = target;
        if(target.paramClassNames == null) {
            target.paramClassNames = new ArrayList<>();
        }
        if(target.paramObjects != null) {
            paramObjectList = new ArrayList(Arrays.asList(target.paramObjects));
        }

        targetClassChooser.init(project, "Choose target class", new ReflectionUtils.FieldSetter(target, "targetClassName"));

        paramAnyObjectForm.init(project, toolWindow, null);

        paramClassSimpleTypeListForm.init(project, toolWindow, target.paramClassNames, paramClassSimpleTypeListForm.new TreeClassChooserFormAssembler());

        paramObjectSimpleTypeListForm.init(project, toolWindow, paramObjectList, paramObjectSimpleTypeListForm.new AnyObjectFormAssembler(paramAnyObjectForm, paramObjectList));

    }

    @Override
    public JPanel getRootPanel() {
        return rootPanel;
    }

    @Override
    public ConstructorSpec getTarget() {
        constructorSpec.paramObjects = paramObjectList.toArray();
        /*
        Not needed, all widgets auto populate
        List l = paramObjectSimpleTypeListForm.getTarget();
        if(l != null && l.size() > 0){
            constructorSpec.paramObjects = l.toArray();
        }
        if(targetClassChooser.hasValue()) {
            constructorSpec.targetClassName = targetClassChooser.getTarget();
        }
        */
        return constructorSpec;
    }

    @Override
    public void clear() {
    }

}
