package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.androware.flow.base.ObjectLoaderSpecBase;

import javax.swing.*;

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

    private ObjectLoaderSpecBase objectLoaderSpecBase;

    public void init(Project project, ObjectLoaderSpecBase objectLoaderSpecBase) {
        objectLoaderClassChooser.init(project, "Choose Object Loader");
        targetObjectClassChooser.init(project, "Choose Target Class");

        this.objectLoaderSpecBase = objectLoaderSpecBase;
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, ObjectLoaderSpecBase target, FormAssembler<CRUDForm> formAssembler) {
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, ObjectLoaderSpecBase target) {
        this.init(project, target);
    }

    @Override
    public JPanel getRootPanel() {
        return objectLoaderFormPanel;
    }

    @Override
    public ObjectLoaderSpecBase getTarget() {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public void populate(ObjectLoaderSpecBase object) {

    }

}
