package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.androware.flow.base.ObjectLoaderSpecBase;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jkirkley on 12/14/16.
 */
public class PropertiesForm implements CRUDForm<Map.Entry> {
    private JTextField keyField;
    private AnyObjectForm anyObjectForm;
    private JPanel rootPanel;
    Map.Entry target;
    CRUDForm parentForm;

    @Override
    public void init(Project project, ToolWindow toolWindow, Map.Entry target, FormAssembler formAssembler, CRUDForm parentForm) {
        this.target = target;
        keyField.setText( target.getKey().toString() );
        anyObjectForm.init(project, toolWindow, target.getValue());
        this.parentForm = parentForm;
    }

    @Override
    public void init(Project project, ToolWindow toolWindow, Map.Entry target, FormAssembler formAssembler) {

    }

    @Override
    public void init(Project project, ToolWindow toolWindow, Map.Entry target) {
    }

    @Override
    public JPanel getRootPanel() {
        return rootPanel;
    }

    @Override
    public Map.Entry getTarget() {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public void populate(Map.Entry object) {

    }

    @Override
    public void done() {
        System.out.println("parentForm: " + parentForm);
        System.out.println("keyField: " + keyField);
        System.out.println("anyobjform: " + anyObjectForm);
        parentForm.handleChildValue(new HashMap.SimpleEntry<>(keyField.getText(), anyObjectForm.getTarget() ));
    }

    @Override
    public void handleChildValue(Object childValue) {

    }
}
