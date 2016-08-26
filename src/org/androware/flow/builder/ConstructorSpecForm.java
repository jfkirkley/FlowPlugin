package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.androware.androbeans.utils.ConstructorSpec;


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

    @Override
    public void init(Project project, ToolWindow toolWindow, ConstructorSpec target, FormAssembler<CRUDForm> formAssembler) {

    }
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
                        ResourcePickerForm.FieldWrap fieldWrap = (ResourcePickerForm.FieldWrap)resourcePickerForm.getTarget();
                        if(v != null) {
                            form.getTypeListComboBox().addItem(fieldWrap.field);
                        }
                    }
                }
            });

        }
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, ConstructorSpec target) {
        constructorSpec = target;
        if(target.paramClassNames == null) {
            target.paramClassNames = new ArrayList<>();
        }
        if(target.paramObjects == null) {
            target.paramObjects = new Object[0];
        }

        targetClassChooser.init(project, toolWindow, target.targetClassName);

        paramAnyObjectForm.init(project, toolWindow, null);
        resourcePickerForm.init(project, toolWindow, null);

        paramClassSimpleTypeListForm.init(project, toolWindow, target.paramClassNames, paramClassSimpleTypeListForm.new TreeClassChooserFormAssembler());

        //paramObjectSimpleTypeListForm.init(project, toolWindow, Arrays.asList(target.paramObjects), paramObjectSimpleTypeListForm.new AnyObjectFormAssembler(paramAnyObjectForm));

        paramObjectSimpleTypeListForm.init(project, toolWindow, Arrays.asList(target.paramObjects), new ParamObjectFormAssembler());

    }

    @Override
    public JPanel getRootPanel() {
        return rootPanel;
    }

    @Override
    public ConstructorSpec getTarget() {
        List l = paramObjectSimpleTypeListForm.getTarget();
        if(l != null && l.size() > 0){
            constructorSpec.paramObjects = l.toArray();
        }
        if(targetClassChooser.hasValue()) {
            constructorSpec.targetClassName = targetClassChooser.getTarget();
        }
        return constructorSpec;
    }

    @Override
    public void clear() {
    }

    @Override
    public void populate(ConstructorSpec object) {

    }
}
