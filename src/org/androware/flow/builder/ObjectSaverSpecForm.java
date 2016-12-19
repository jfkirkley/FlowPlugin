package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.androware.androbeans.utils.ReflectionUtils;
import org.androware.flow.base.ObjectSaverSpecBase;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by jkirkley on 8/21/16.
 */
public class ObjectSaverSpecForm implements CRUDForm<ObjectSaverSpecBase>{
    private JTextField objectIDtextField;
    private ClassChooserWidget saverClassChooser;
    private JPanel rootPanel;
    private ObjectSaverSpecBase target;

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
    public void populate(ObjectSaverSpecBase object) {
    }

    @Override
    public void done() {

    }

    @Override
    public void handleChildValue(Object childValue) {

    }

}
