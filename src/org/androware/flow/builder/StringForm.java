package org.androware.flow.builder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;

/**
 * Created by jkirkley on 8/22/16.
 */
public class StringForm implements CRUDForm<String> {
    private JTextField textField;
    private JPanel rootPanel;
    String string;

    @Override
    public void init(Project project, ToolWindow toolWindow, String target, FormAssembler formAssembler) {

    }

    @Override
    public void init(Project project, ToolWindow toolWindow, String target) {
        string = (String)target;
    }


    @Override
    public JPanel getRootPanel() {
        return rootPanel;
    }

    @Override
    public String getTarget() {
        return string;
    }

    @Override
    public void clear() {

    }

    @Override
    public void populate(String object) {

    }

    @Override
    public void done() {

    }

}
