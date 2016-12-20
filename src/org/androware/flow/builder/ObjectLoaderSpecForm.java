package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.androware.androbeans.utils.ReflectionUtils;
import org.androware.aop.AOP;
import org.androware.flow.base.ObjectLoaderSpecBase;

import javax.swing.*;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jkirkley on 8/20/16.
 */
public class ObjectLoaderSpecForm implements CRUDForm<ObjectLoaderSpecBase> {
    private JPanel objectLoaderFormPanel;
    private ClassChooserWidget objectLoaderClassChooser;
    private ClassChooserWidget targetObjectClassChooser;
    private JTextField objectIdTextField;
    private JTextField aliasTextField;
    private JRadioButton prePreRadioButton;
    private JRadioButton postPreRadioButton;
    private JRadioButton prePostRadioButton;
    private JRadioButton postPostRadioButton;
    private JRadioButton onDemandRadioButton;
    private JRadioButton flowInitRadioButton;
    private JCheckBox autoCreateCheckBox;
    private ComboBoxCRUDForm propertiesComboBoxCRUDForm;

    private ObjectLoaderSpecBase objectLoaderSpecBase;

    private CRUDForm parentForm;

    ButtonGroup whenButtonGroup = new ButtonGroup();
    HashMap<String, ButtonModel> whenMap = new HashMap<>();

    @Override
    public void done() {
        ButtonModel model = whenButtonGroup.getSelection();
        if (model != null) {
            objectLoaderSpecBase.when = (String) Utils.getKeyForValue(whenMap, model);
        }
        objectLoaderSpecBase.objectClassName = targetObjectClassChooser.getClassName();
        objectLoaderSpecBase.objectLoaderClassName = objectLoaderClassChooser.getClassName();
        objectLoaderSpecBase.objectId = objectIdTextField.getText();
        objectLoaderSpecBase.alias = aliasTextField.getText();
        objectLoaderSpecBase.autoCreate = autoCreateCheckBox.isSelected();

        if(parentForm != null) {
            parentForm.handleChildValue(objectLoaderSpecBase);
        }


    }

    @Override
    public void handleChildValue(Object childValue) {
        if(childValue instanceof HashMap.SimpleEntry) {
            HashMap.SimpleEntry simpleEntry = (HashMap.SimpleEntry) childValue;
            objectLoaderSpecBase.properties.put((String)simpleEntry.getKey(), simpleEntry.getValue());
            objectLoaderSpecBase.properties.remove("");  // remove  key added if addButton was pressed

            propertiesComboBoxCRUDForm.getjComboBoxCRUDWrapper().reset(true);
        }
    }


    @Override
    public void populate(ObjectLoaderSpecBase objectLoaderSpecBase) {
        CompFactory.setTextfieldVal(objectIdTextField, objectLoaderSpecBase, "objectId");
        CompFactory.setTextfieldVal(aliasTextField, objectLoaderSpecBase, "alias");

        if (objectLoaderSpecBase.when != null) {
            whenButtonGroup.setSelected(whenMap.get(objectLoaderSpecBase.when), true);
        }
        autoCreateCheckBox.setSelected(objectLoaderSpecBase.autoCreate);
    }

    private void addRadioButton(JRadioButton button, String key) {
        whenButtonGroup.add(button);
        whenMap.put(key, button.getModel());
    }

    public void init(Project project, ObjectLoaderSpecBase objectLoaderSpecBase) {
        objectLoaderClassChooser.init(project, "Choose Object Loader", new ReflectionUtils.FieldSetter(objectLoaderSpecBase, "objectLoaderClassName"));
        targetObjectClassChooser.init(project, "Choose Target Class", new ReflectionUtils.FieldSetter(objectLoaderSpecBase, "objectClassName"));

        this.objectLoaderSpecBase = objectLoaderSpecBase;


        addRadioButton(prePreRadioButton, ObjectLoaderSpecBase.ON_PRE_PRE_STEP_TRANS);
        addRadioButton(postPreRadioButton, ObjectLoaderSpecBase.ON_POST_PRE_STEP_TRANS);
        addRadioButton(prePostRadioButton, ObjectLoaderSpecBase.ON_PRE_POST_STEP_TRANS);
        addRadioButton(postPostRadioButton, ObjectLoaderSpecBase.ON_POST_POST_STEP_TRANS);
        addRadioButton(onDemandRadioButton, ObjectLoaderSpecBase.ON_DEMAND);
        addRadioButton(flowInitRadioButton, ObjectLoaderSpecBase.ON_FLOW_INIT);

        this.populate(objectLoaderSpecBase);


    }

    @Override
    public void init(Project project, ToolWindow toolWindow, ObjectLoaderSpecBase target, FormAssembler formAssembler, CRUDForm parentForm) {
        this.parentForm = parentForm;
        init(project, toolWindow, target);
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, ObjectLoaderSpecBase target, FormAssembler formAssembler) {
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, ObjectLoaderSpecBase target) {
        this.init(project, target);
        if(false) {
            propertiesComboBoxCRUDForm.init(project,
                    new CompFactory.DefaultCRUDEditorImpl<HashMap.SimpleEntry>(project, toolWindow, PropertiesForm.class, HashMap.SimpleEntry.class, null,
                            new CompFactory.ObjectBuilder<HashMap.SimpleEntry>() {
                                @Override
                                public HashMap.SimpleEntry build() {
                                    return new HashMap.SimpleEntry("", "");
                                }
                            }), new ReflectionUtils.FieldSetter(target, "properties"), true);
        } else {
            try {
                propertiesComboBoxCRUDForm.init(project,
                        AOP.t(CompFactory.DefaultCRUDEditorImpl.class, project, toolWindow, PropertiesForm.class, HashMap.SimpleEntry.class, null,
                                new CompFactory.ObjectBuilder<HashMap.SimpleEntry>() {
                                    @Override
                                    public HashMap.SimpleEntry build() {
                                        return new HashMap.SimpleEntry("", "");
                                    }
                                }, null, this), AOP.t(ReflectionUtils.FieldSetter.class, target, "properties"), true);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public JPanel getRootPanel() {
        return objectLoaderFormPanel;
    }

    @Override
    public ObjectLoaderSpecBase getTarget() {
        return objectLoaderSpecBase;
    }

    @Override
    public void clear() {

    }

}
