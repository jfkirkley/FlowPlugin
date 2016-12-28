package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.androware.androbeans.utils.ReflectionUtils;
import org.androware.flow.base.ObjectLoaderSpecBase;
import org.androware.flow.base.ObjectSaverSpecBase;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

/**
 * Created by jkirkley on 8/21/16.
 */
public class ObjectSaverSpecForm implements CRUDForm<ObjectSaverSpecBase>{
    private JTextField objectIDtextField;
    private ClassChooserWidget saverClassChooser;
    private JPanel rootPanel;
    private JRadioButton changeRadioButton;
    private JRadioButton transitionRadioButton;
    private JRadioButton flowEndRadioButton;
    private ComboBoxCRUDForm propertiesComboBoxCRUDForm;
    private ObjectSaverSpecBase target;

    ButtonGroup triggerButtonGroup = new ButtonGroup();
    HashMap<String, ButtonModel> triggerMap = new HashMap<>();

    @Override
    public void init(Project project, ToolWindow toolWindow, ObjectSaverSpecBase target, FormAssembler formAssembler, CRUDForm parentForm) {
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, ObjectSaverSpecBase target, FormAssembler formAssembler) {

    }

    @Override
    public void init(Project project, ToolWindow toolWindow, ObjectSaverSpecBase target) {
        this.target = target;
        saverClassChooser.init(project, "Choose Saver Class", new ReflectionUtils.FieldSetter(target, "objectSaverClassName"));
        CompFactory.setTextfieldVal(objectIDtextField, target, "objectId");

        CompFactory.addRadioButton(changeRadioButton, ObjectSaverSpecBase.CHANGE_TRIGGER, triggerButtonGroup, triggerMap);
        CompFactory.addRadioButton(transitionRadioButton, ObjectSaverSpecBase.TRANSITION_TRIGGER, triggerButtonGroup, triggerMap);
        CompFactory.addRadioButton(flowEndRadioButton, ObjectSaverSpecBase.FLOW_END_TRIGGER, triggerButtonGroup, triggerMap);



        propertiesComboBoxCRUDForm.init(project,
                new CompFactory.DefaultCRUDEditorImpl<HashMap.SimpleEntry>(project, toolWindow, PropertiesForm.class, HashMap.SimpleEntry.class, null,
                        new CompFactory.ObjectBuilder<HashMap.SimpleEntry>() {
                            @Override
                            public HashMap.SimpleEntry build() {
                                return new HashMap.SimpleEntry("", "");
                            }
                        }, null, this), new ReflectionUtils.FieldSetter(target, "properties"), true);

        objectIDtextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                target.objectId = objectIDtextField.getText();
                System.out.println(target.objectId);
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {

            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });

        populate(target);
    }

    @Override
    public JPanel getRootPanel() {
        return rootPanel;
    }

    @Override
    public ObjectSaverSpecBase getTarget() {
        return target != null? target: new ObjectSaverSpecBase();
    }

    @Override
    public void clear() {

    }

    @Override
    public void populate(ObjectSaverSpecBase target) {
        if (target.saveTrigger != null) {
            triggerButtonGroup.setSelected(triggerMap.get(target.saveTrigger), true);
        }
    }

    @Override
    public void done() {
        ButtonModel model = triggerButtonGroup.getSelection();
        if (model != null) {
            target.saveTrigger = (String) Utils.getKeyForValue(triggerMap, model);
        }

    }

    @Override
    public void handleChildValue(Object childValue) {
        if(childValue instanceof HashMap.SimpleEntry) {
            HashMap.SimpleEntry simpleEntry = (HashMap.SimpleEntry) childValue;
            target.properties.put((String)simpleEntry.getKey(), simpleEntry.getValue());
            target.properties.remove("");  // remove  key added if addButton was pressed

            propertiesComboBoxCRUDForm.getjComboBoxCRUDWrapper().reset(true);
        }


    }

}
