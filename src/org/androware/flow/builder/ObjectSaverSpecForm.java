package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.androware.flow.base.ObjectSaverSpecBase;

import javax.swing.*;

/**
 * Created by jkirkley on 8/21/16.
 */
public class ObjectSaverSpecForm implements CRUDForm<ObjectSaverSpecBase>{
    private JTextField objectIDtextField;
    private ClassChooserWidget saverClassChooser;
    private JPanel rootPanel;
    private ObjectSaverSpecBase target;

    @Override
    public void init(Project project, ToolWindow toolWindow, ObjectSaverSpecBase target, FormAssembler<CRUDForm> formAssembler) {

    }

    @Override
    public void init(Project project, ToolWindow toolWindow, ObjectSaverSpecBase target) {
        this.target = target;
        saverClassChooser.init(project, "Choose Saver Class");
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
}
